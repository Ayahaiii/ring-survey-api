package com.monetware.ringsurvey.system.util.file;

import java.awt.image.BufferedImage;
import java.io.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.monetware.ringsurvey.system.util.codec.UUIDUtil;
import net.coobird.thumbnailator.Thumbnails;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;

/**
 * 图片压缩工具类
 */
public class ImgUtil {

    /**
     * 按比例压缩
     *
     * @param originFileName 源文件
     * @param targetFileName 目标文件
     * @param scale          比例
     * @param format         图片格式
     * @throws IOException
     */
    public void resizeFix(String originFileName, String targetFileName, double scale, String format) throws IOException {
        Thumbnails.of(originFileName).scale(scale).outputFormat(format).toFile(targetFileName);
    }

    /**
     * 指定宽高压缩
     *
     * @param originFileName 源文件
     * @param targetFileName 目标文件
     * @param x              宽度
     * @param y              高度
     * @param format         图片格式
     * @throws IOException
     */
    public void resizeFix(String originFileName, String targetFileName, int x, int y, String format) throws IOException {
        Thumbnails.of(originFileName).size(x, y).outputFormat(format).toFile(targetFileName);
    }

    /**
     * 压缩图片大小
     *
     * @param originFileName
     * @param targetFileName
     * @param scale
     * @throws IOException
     */
    public static void zipImg(String originFileName, String targetFileName, float scale) throws IOException {
        //图片尺寸不变，压缩图片文件大小outputQuality实现,参数1为最高质量
        Thumbnails.of(originFileName).scale(1f).outputQuality(scale).toFile(targetFileName);
    }

    /**
     * 转换文件格式
     *
     * @param originFileName
     * @param targetFileName
     * @param format
     * @throws IOException
     */
    public static void changeFormat(String originFileName, String targetFileName, String format) throws IOException {
        //用outputFormat(图像格式)转换图片格式，保持原尺寸不变
        Thumbnails.of(originFileName).scale(1f).outputFormat(format).toFile(targetFileName);
    }

    /**
     * 图片上传并压缩
     *
     * @param files
     * @param desPath
     * @param desFileSize
     * @param accuracy
     * @return
     */
    public static List<Map<String, Object>> compressPicForScale(MultipartFile[] files, String desPath, long desFileSize, double accuracy) {
        List<Map<String, Object>> result = new ArrayList<>();
        if (StringUtils.isBlank(desPath)) {
            return null;
        }

        File filePath = new File(desPath);
        if (!filePath.exists()) {
            filePath.mkdirs();
        }

        if (files != null && files.length > 0) {
            for (int i = 0; i < files.length; i++) {
                MultipartFile file = files[i];

                if (!file.isEmpty()) {
                    String fileName = file.getOriginalFilename().trim();
                    String suffix = fileName.substring(fileName.lastIndexOf("."));// 后缀
                    String randomFileName = UUIDUtil.getRandomUUID() + suffix;

                    try {
                        // 先转换成jpg
                        Thumbnails.Builder builder = Thumbnails.of(file.getInputStream()).outputFormat("jpg").scale(1f);
                        // 写入到内存
                        ByteArrayOutputStream bs = new ByteArrayOutputStream(); //字节输出流（写入到内存）
                        builder.toOutputStream(bs);
                        // 递归压缩，直到目标文件大小小于desFileSize
                        byte[] bytes = compressPicCycle(bs.toByteArray(), desFileSize, accuracy);
                        // 输出到文件
                        File desFile = new File(desPath + "/" + randomFileName);
                        FileOutputStream fos = new FileOutputStream(desFile);
                        fos.write(bytes);
                        fos.flush();
                        fos.close();
                        Map<String, Object> map = new HashMap<>();
                        map.put("fileName", fileName);
                        map.put("randomFileName", randomFileName);
                        map.put("fileSize", desFile.length());
                        result.add(map);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return result;
    }

    private static byte[] compressPicCycle(byte[] bytes, long desFileSize, double accuracy) throws IOException {
        // File srcFileJPG = new File(desPath);
        long srcFileSizeJPG = bytes.length;
        // 2、判断大小，如果小于500kb，不压缩；如果大于等于500kb，压缩
        if (srcFileSizeJPG <= desFileSize * 1024) {
            return bytes;
        }
        // 计算宽高
        BufferedImage bim = ImageIO.read(new ByteArrayInputStream(bytes));
        int srcWidth = bim.getWidth();
        int srcHeight = bim.getHeight();
        int desWidth = new BigDecimal(srcWidth).multiply(new BigDecimal(accuracy)).intValue();
        int desHeight = new BigDecimal(srcHeight).multiply(new BigDecimal(accuracy)).intValue();

        ByteArrayOutputStream baos = new ByteArrayOutputStream(); //字节输出流（写入到内存）
        Thumbnails.of(new ByteArrayInputStream(bytes)).size(desWidth, desHeight).outputQuality(accuracy).toOutputStream(baos);
        return compressPicCycle(baos.toByteArray(), desFileSize, accuracy);
    }

}
