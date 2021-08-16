package com.monetware.ringsurvey.business.service;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.monetware.ringsurvey.business.dao.AppVersionDao;
import com.monetware.ringsurvey.business.pojo.po.BaseAppVersion;
import com.monetware.ringsurvey.business.pojo.vo.app.AppVersionVO;
import com.monetware.ringsurvey.system.base.ErrorCode;
import com.monetware.ringsurvey.system.base.PageList;
import com.monetware.ringsurvey.system.base.PageParam;
import com.monetware.ringsurvey.system.exception.ServiceException;
import com.monetware.ringsurvey.system.util.file.FileUtil;
import com.monetware.threadlocal.ThreadLocalManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import tk.mybatis.mapper.entity.Example;


import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author Simo
 * @date 2020-03-03
 */
@Slf4j
@Service
public class AppService {

    @Autowired
    private AppVersionDao appVersionDao;

    @Value("${fileUrl.upload}")
    private String filePath;

    public String uploadFile( MultipartFile file) {
        if (file == null) {
            throw new ServiceException(ErrorCode.CUSTOM_MSG, "APK文件为空");
        }
        String path = "/app/android/apk/";
        List<Map<String, Object>> resList = FileUtil.uploadFiles(new MultipartFile[]{file}, filePath + path);
        return path + resList.get(0).get("randomFileName").toString();
    }

    /**
     * 上传安卓APK
     * @param appVersionVO
     * @return
     */
    public int insertAppVersion(AppVersionVO appVersionVO) {
        Date now = new Date();
        BaseAppVersion appVersion = new BaseAppVersion();
        BeanUtils.copyProperties(appVersionVO, appVersion);
        appVersion.setStatus(0);
        appVersion.setCreateTime(now);
        appVersion.setCreateUser(ThreadLocalManager.getUserId());
        appVersion.setLastModifyTime(now);
        appVersion.setLastModifyUser(ThreadLocalManager.getUserId());
        return appVersionDao.insertSelective(appVersion);
    }

    /**
     * 获取安卓APK列表
     * @param pageParam
     * @return
     */
    public PageList<Page> getAppVersionList(PageParam pageParam) {
        Page page = PageHelper.startPage(pageParam.getPageNum(), pageParam.getPageSize());
        Example example = new Example(BaseAppVersion.class);
        example.orderBy("versionCode").desc();
        appVersionDao.selectByExample(example);
        return new PageList<>(page);
    }

    /**
     * 获取最新已启用版本
     * @return
     */
    public String getAppVersion() {
        Example example = new Example(BaseAppVersion.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("status", 1);
        example.orderBy("versionCode").desc();
        List<BaseAppVersion> res = appVersionDao.selectByExample(example);
        if (res.size() > 0) {
            return res.get(0).getVersionUrl();
        }
        return null;
    }

    /**
     * 修改状态
     * @param appVersion
     * @return
     */
    public int updateAppVersion(BaseAppVersion appVersion) {
        return appVersionDao.updateByPrimaryKeySelective(appVersion);
    }

    /**
     * 删除APK
     * @param id
     * @return
     */
    public int deleteAppVersion(Integer id) {
        return appVersionDao.deleteByPrimaryKey(id);
    }

}
