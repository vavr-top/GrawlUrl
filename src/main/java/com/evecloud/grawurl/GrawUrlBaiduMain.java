package com.evecloud.grawurl;

import com.evecloud.grawurl.model.BaiduUrlBean;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.FileOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author: zhaoyk
 * @date: 2019-5-7 16:31
 * @description:
 */

public class GrawUrlBaiduMain {
    private static String urll = "http://www.baidu.com/s?wd=";

    private static List<BaiduUrlBean> getNews(String url, int num) {
        List<BaiduUrlBean> baiduUrlBeans = new ArrayList<BaiduUrlBean>();

        try {
            Document doc = Jsoup.connect(url).get();
            Element element = doc.getElementById("content_left");
            Elements f13 = new Elements();
            Elements a = null;

            for (int i = num + 1; i <= (num + 10); i++) {
                BaiduUrlBean baiduUrlBean = new BaiduUrlBean();
                f13.clear();
                a = null;

                Element result = element.getElementById(String.valueOf(i));
                Elements add = result.select("a");
                f13 = result.getElementsByClass("f13");

                if (f13 != null && f13.first() != null) {
                    a = f13.first().select("a");
                }

                baiduUrlBean.setCompanyName(add.first().text());
                System.out.println("--------------------- " + i + " ---------------------");
                System.out.println(add.first().text());
                if (!Objects.isNull(a) && !Objects.isNull(a.first())) {
                    baiduUrlBean.setDomainName(a.first().text());
                    baiduUrlBean.setLink(a.first().attr("href"));
                    System.out.println(a.first().text());
                    System.out.println(a.first().attr("href"));
                } else {
                    baiduUrlBean.setLink(add.first().attr("href"));
                }

                baiduUrlBeans.add(baiduUrlBean);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return baiduUrlBeans;
    }

    private static List<BaiduUrlBean> getResult(String question) {
        List<BaiduUrlBean> baiduUrlBeans = new ArrayList<BaiduUrlBean>();
        int pn = 0;
        List<BaiduUrlBean> news;
        do {
            String url = "";
            try {
                url = urll + URLEncoder.encode(question, "utf-8") + "&pn=" + pn * 10;
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            news = getNews(url, pn * 10);
            baiduUrlBeans.addAll(news);
            pn++;
        } while (news.size() >= 10);

        return baiduUrlBeans;
    }

    public static void main(String[] args) throws Exception {
        List<BaiduUrlBean> result = getResult("山东**集团");
        createExcel("C:/logs/grawurl/test.xls", result);
    }

    public static void createExcel(String excelName, List<BaiduUrlBean> baiduUrlBeans) throws Exception {
        //创建工作簿
        XSSFWorkbook wb = new XSSFWorkbook();
        //创建一个sheet
        XSSFSheet sheet = wb.createSheet();

        // 创建单元格样式
        XSSFCellStyle style = wb.createCellStyle();
//        style.setFillForegroundColor((short) 4); //设置要添加表格北京颜色
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND); //solid 填充
//        style.setAlignment(XSSFCellStyle.ALIGN_CENTER); //文字水平居中
//        style.setVerticalAlignment(XSSFCellStyle.VERTICAL_CENTER);//文字垂直居中
        style.setBorderBottom(BorderStyle.THIN); //底边框加黑
        style.setBorderLeft(BorderStyle.THIN);  //左边框加黑
        style.setBorderRight(BorderStyle.THIN); // 有边框加黑
        style.setBorderTop(BorderStyle.THIN); //上边框加黑

        //为单元格添加背景样式
        for (int i = 0; i < baiduUrlBeans.size(); i++) {
            BaiduUrlBean baiduUrlBean = baiduUrlBeans.get(i);
            Row row = sheet.createRow(i); //创建行

            Cell cell = row.createCell(0);
            cell.setCellStyle(style);
            cell.setCellValue(baiduUrlBean.getCompanyName());
            Cell cell1 = row.createCell(1);
            cell1.setCellStyle(style);
            cell1.setCellValue(baiduUrlBean.getDomainName());
            Cell cell2 = row.createCell(2);
            cell2.setCellStyle(style);
            cell2.setCellValue(baiduUrlBean.getLink());
        }

        //将数据写入文件
        FileOutputStream out = new FileOutputStream(excelName);
        wb.write(out);
        wb.close();
    }
}
