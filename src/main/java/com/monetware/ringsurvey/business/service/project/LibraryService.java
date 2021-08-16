package com.monetware.ringsurvey.business.service.project;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.monetware.ringsurvey.business.dao.*;
import com.monetware.ringsurvey.business.pojo.constants.LibraryConstants;
import com.monetware.ringsurvey.business.pojo.constants.ProjectConstants;
import com.monetware.ringsurvey.business.pojo.dto.library.*;
import com.monetware.ringsurvey.business.pojo.dto.qnaire.QuestionnaireInitDTO;
import com.monetware.ringsurvey.business.pojo.dto.qnaire.QuestionnaireParseDTO;
import com.monetware.ringsurvey.business.pojo.po.*;
import com.monetware.ringsurvey.business.pojo.po.payOrder.BasePayOrder;
import com.monetware.ringsurvey.business.pojo.vo.library.CommentsInfoVO;
import com.monetware.ringsurvey.business.pojo.vo.library.FavoriteVO;
import com.monetware.ringsurvey.business.pojo.vo.library.LibraryListVO;
import com.monetware.ringsurvey.business.pojo.vo.library.QnaireShareVO;
import com.monetware.ringsurvey.system.base.ErrorCode;
import com.monetware.ringsurvey.system.base.PageList;
import com.monetware.ringsurvey.system.exception.ServiceException;
import com.monetware.ringsurvey.system.util.file.FileUtil;
import com.monetware.ringsurvey.system.util.file.ImgUtil;
import com.monetware.ringsurvey.system.util.survml.SurvmlUtil;
import com.monetware.threadlocal.ThreadLocalManager;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import tk.mybatis.mapper.entity.Example;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author Linked
 * @date 2020/5/27 16:15
 */
@Slf4j
@Service
public class LibraryService {

    @Autowired
    private UserDao userDao;

    @Autowired
    private LibraryDao libraryDao;

    @Autowired
    private QuestionnaireDao questionnaireDao;

    @Autowired
    private PermissionDao permissionDao;

    @Autowired
    private PayOrderDao orderDao;

    @Autowired
    private CommentDao commentDao;

    @Autowired
    private LibraryRelationDao relationDao;

    @Value("${fileUrl.upload}")
    private String filePath;

    @Value("${fileUrl.qnaire}")
    private String questionnaireDir;

    @Autowired
    private QuestionnaireService questionnaireService;

    /**
     * 初始化信息
     *
     * @param questionnaireId
     * @return
     */
    public QuestionnaireInitDTO getInitInfo(Integer questionnaireId) {
        BaseQuestionnaire baseQuestionnaire = questionnaireDao.selectByPrimaryKey(questionnaireId);
        QuestionnaireInitDTO res = new QuestionnaireInitDTO();
        BeanUtils.copyProperties(baseQuestionnaire, res);
        return res;
    }

    /**
     * 发布问卷市场
     *
     * @param file
     * @param questionnaireId
     * @param price
     * @param description
     * @return
     */
    public Integer saveLibrary(MultipartFile[] file, Integer questionnaireId, BigDecimal price, String description, Integer ifFree) {
        int index = 0;
        String pre = "/questionnaires/library/" + ThreadLocalManager.getUserId() + "/";
        String path = filePath + pre;
        Example example = new Example(BaseQuestionnaireLibrary.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("questionnaireId", questionnaireId);
        criteria.andEqualTo("status", LibraryConstants.STATUS_MARKETING);
        BaseQuestionnaireLibrary questionnaireLibrary = libraryDao.selectOneByExample(example);
        if (questionnaireLibrary == null) {
            //
            BaseQuestionnaire questionnaire = questionnaireDao.selectByPrimaryKey(questionnaireId);
            questionnaire.setLastModifyTime(new Date());
            questionnaire.setLastModifyUser(ThreadLocalManager.getUserId());
            questionnaire.setIsLibrary(LibraryConstants.LIBRARY_YES);
            questionnaireDao.updateByPrimaryKeySelective(questionnaire);
            //
            BaseQuestionnaireLibrary library = new BaseQuestionnaireLibrary();
            BeanUtils.copyProperties(questionnaire, library);
            List<Map<String, Object>> res = ImgUtil.compressPicForScale(file, path, LibraryConstants.PIC_SIZE, LibraryConstants.PIC_ACCURACY);
            String fileName = res.get(0).get("randomFileName").toString();
            library.setId(null);
            // TODO 状态
            library.setStatus(LibraryConstants.STATUS_MARKETING);
            library.setImageUrl(pre + fileName);
            library.setPrice(price);
            library.setQuestionnaireId(questionnaireId);
            library.setCreateTime(new Date());
            library.setCreateUser(ThreadLocalManager.getUserId());
            library.setDescription(description);
            library.setIfFree(ifFree);
            index = libraryDao.insertSelective(library);
        } else {
            // 更新
            questionnaireLibrary.setDescription(description);
            questionnaireLibrary.setPrice(price);
            if (file.length > 0) {
                // 删除原先照片
                String ImageUrl = filePath + questionnaireLibrary.getImageUrl();
                File image = new File(ImageUrl);
                if (image.exists()) {
                    image.delete();
                }
                List<Map<String, Object>> res = ImgUtil.compressPicForScale(file, path, LibraryConstants.PIC_SIZE, LibraryConstants.PIC_ACCURACY);
                String fileName = res.get(0).get("randomFileName").toString();
                questionnaireLibrary.setImageUrl(pre + fileName);
            }
            index = libraryDao.updateByPrimaryKeySelective(questionnaireLibrary);
        }
        return index;
    }


    /**
     * 问卷市场列表
     *
     * @param listVO
     * @return
     */
    public PageList<Page> getLibraryList(LibraryListVO listVO) {
        listVO.setUserId(ThreadLocalManager.getUserId());
        // 筛选购买
        Example example = new Example(BaseLibraryRelation.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("userId", ThreadLocalManager.getUserId());
        List<BaseLibraryRelation> relationList = relationDao.selectByExample(example);
        Page page = PageHelper.startPage(listVO.getPageNum(), listVO.getPageSize());
        List<LibraryListDTO> libraryList = libraryDao.getLibraryList(listVO);
        for (LibraryListDTO listDTO : libraryList) {
            for (BaseLibraryRelation relation : relationList) {
                if (relation != null && relation.getType() == LibraryConstants.TYPE_BUY && relation.getLibraryId() == listDTO.getId()) {
                    listDTO.setIfBuy(1);
                }
                if (relation != null && relation.getType() == LibraryConstants.TYPE_COLLECT && relation.getLibraryId() == listDTO.getId()) {
                    listDTO.setIfStar(1);
                }
            }
            // 处理图片
            if (StringUtils.isNotBlank(listDTO.getImageUrl())) {
                File temp = new File(filePath + listDTO.getImageUrl());
                if (!temp.exists()) {
                    listDTO.setImageUrl(null);
                }
            }
        }
        return new PageList<>(page);
    }

    /**
     * 详情
     *
     * @param id
     * @return
     */
    public LibraryDetailDTO getLibraryDetail(Integer id) {
        Integer userId = ThreadLocalManager.getUserId();
        LibraryDetailDTO detailDTO = libraryDao.getLibraryDetail(id, userId);
        QuestionnaireParseDTO parseDTO = SurvmlUtil.getQuestionsInfoByXml(detailDTO.getXmlContent());
        Example example = new Example(BaseLibraryRelation.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("userId", ThreadLocalManager.getUserId());
        List<BaseLibraryRelation> relationList = relationDao.selectByExample(example);
        for (BaseLibraryRelation relation : relationList) {
            if (relation.getType() == LibraryConstants.TYPE_BUY && relation.getLibraryId().equals(id)) {
                detailDTO.setIfBuy(1);
            }
            if (relation.getType() == LibraryConstants.TYPE_COLLECT && relation.getLibraryId().equals(id)) {
                detailDTO.setIfStar(1);
            }
        }
        BaseQuestionnaireLibrary library = libraryDao.selectByPrimaryKey(id);
        library.setViewCount(library.getViewCount() + 1);
        libraryDao.updateByPrimaryKeySelective(library);
        detailDTO.setQnaireNum(parseDTO.getQuestions().size());
        // 处理图片
        if (StringUtils.isNotBlank(detailDTO.getImageUrl())) {
            File temp = new File(filePath + detailDTO.getImageUrl());
            if (!temp.exists()) {
                detailDTO.setImageUrl(null);
            }
        }
        return detailDTO;
    }

    /**
     * 发布详情
     *
     * @param questionnaireId
     * @return
     */
    public PubDetailDTO getPubDetail(Integer questionnaireId) {
        PubDetailDTO detailDTO = new PubDetailDTO();
        Example example = new Example(BaseQuestionnaireLibrary.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("questionnaireId", questionnaireId);
        criteria.andEqualTo("status", LibraryConstants.STATUS_MARKETING);
        BaseQuestionnaireLibrary library = libraryDao.selectOneByExample(example);
        BeanUtils.copyProperties(library, detailDTO);
        return detailDTO;
    }

    /**
     * 撤销问卷市场
     *
     * @param id
     * @return
     */
    public Integer deleteLibrary(Integer id) {
        BaseQuestionnaire questionnaire = questionnaireDao.selectByPrimaryKey(id);
        questionnaire.setIsLibrary(LibraryConstants.LIBRARY_NO);
        Example example = new Example(BaseQuestionnaireLibrary.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("questionnaireId", id);
        criteria.andEqualTo("status", LibraryConstants.STATUS_MARKETING);
        BaseQuestionnaireLibrary library = libraryDao.selectOneByExample(example);
        library.setStatus(LibraryConstants.STATUS_OFF);
        libraryDao.updateByPrimaryKeySelective(library);
        return questionnaireDao.updateByPrimaryKey(questionnaire);

    }

    /**
     * 获取免费问卷数量
     *
     * @return
     */
    public Integer getFreeNum() {
        BaseUser user = userDao.selectByPrimaryKey(ThreadLocalManager.getUserId());
        Example example = new Example(BasePermission.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("id", user.getRole());
        BasePermission permission = permissionDao.selectOneByExample(example);
        return permission.getFreeNum() - user.getUseFreeNum();
    }

    /**
     * 复制
     *
     * @param libraryId
     * @return
     */
    public int insertQuestionnaire(Integer libraryId) {
        BaseQuestionnaireLibrary library = libraryDao.selectByPrimaryKey(libraryId);
        BaseQuestionnaire questionnaire = new BaseQuestionnaire();
        BeanUtils.copyProperties(library, questionnaire);
        questionnaire.setId(null);
        questionnaire.setCreateUser(ThreadLocalManager.getUserId());
        questionnaire.setCreateTime(new Date());
        questionnaire.setLastModifyTime(new Date());
        questionnaire.setIsLibrary(LibraryConstants.LIBRARY_NO);
        questionnaire.setIsDelete(ProjectConstants.DELETE_NO);
        // TODO 始终复制最新的问卷模板
        int row = questionnaireDao.insertSelective(questionnaire);
        // 创建survey.js
        String filePathStr = questionnaireDir + "/js/design/" + questionnaire.getId();
        questionnaireService.createSurveyJS(filePathStr, questionnaire.getXmlContent());
        return row;
    }


    /**
     * 发表评分,评论
     *
     * @param commentsInfoVO
     * @return
     */
    public Integer saveCommentsInfo(CommentsInfoVO commentsInfoVO) {
        commentsInfoVO.setCreateTime(new Date());
        if (commentsInfoVO.getReplyId() == null) {
            Example example = new Example(BaseQuestionnaireLibrary.class);
            Example.Criteria criteria = example.createCriteria();
            criteria.andEqualTo("id", commentsInfoVO.getLibraryId());
            BaseQuestionnaireLibrary library = libraryDao.selectOneByExample(example);
            library.setCommentCount(library.getCommentCount() + 1);
            library.setRate(commentsInfoVO.getScore());
            libraryDao.updateByPrimaryKeySelective(library);
        }
        return commentDao.insertSelective(commentsInfoVO);
    }

    /**
     * 查询评论
     *
     * @param libraryId
     * @return
     */
    public List<QnaireCommentsDTO> getCommentsInfo(Integer libraryId) {
        List<QnaireCommentsDTO> commentsDtoList = commentDao.getCommentsInfo(libraryId);
        return sortData(commentsDtoList);
    }

    /**
     * 将无序的数据整理成有层级关系的数据
     *
     * @param commentsDtoList
     * @return
     */
    private List<QnaireCommentsDTO> sortData(List<QnaireCommentsDTO> commentsDtoList) {
        List<QnaireCommentsDTO> list = new ArrayList<>();
        for (int i = 0; i < commentsDtoList.size(); i++) {
            QnaireCommentsDTO parentDto = commentsDtoList.get(i);
            List<QnaireCommentsDTO> children = new ArrayList<>();
            for (QnaireCommentsDTO childrenDto : commentsDtoList) {
                if (childrenDto.getReplyId() == null) {
                    continue;
                }
                if (parentDto.getId().equals(childrenDto.getReplyId())) {
                    children.add(childrenDto);
                }
            }
            parentDto.setChildren(children);
            //最外层的数据只添加 pid 为空的评论，其他评论在父评论的 children 下
            if (parentDto.getReplyId() == null) {
                list.add(parentDto);
            }
        }
        return list;
    }

    /**
     * 收藏/取消收藏
     *
     * @param favoriteVO
     * @return
     */
    public Integer saveStar(FavoriteVO favoriteVO) {
        // 取消收藏
        if (favoriteVO.getStarType() == null) {
            Example example = new Example(BaseLibraryRelation.class);
            Example.Criteria criteria = example.createCriteria();
            criteria.andEqualTo("userId", ThreadLocalManager.getUserId());
            criteria.andEqualTo("libraryId", favoriteVO.getLibraryId());
            return relationDao.deleteByExample(example);
        }
        // 收藏
        BaseLibraryRelation relation = new BaseLibraryRelation();
        relation.setLibraryId(favoriteVO.getLibraryId());
        relation.setUserId(ThreadLocalManager.getUserId());
        relation.setType(LibraryConstants.TYPE_COLLECT);
        return relationDao.insert(relation);
    }

    /**
     * 我共享的问卷
     *
     * @param shareVO
     * @return
     */
    public List<QnaireShareDTO> getMyQnaire(QnaireShareVO shareVO) {
        shareVO.setUserId(ThreadLocalManager.getUserId());
        return libraryDao.getMyShareList(shareVO);
    }


}
