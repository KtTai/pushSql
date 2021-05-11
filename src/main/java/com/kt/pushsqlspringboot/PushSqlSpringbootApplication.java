package com.kt.pushsqlspringboot;

import com.kt.pushsqlspringboot.service.UpdatePushSqlService;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.system.SystemProperties;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

@SpringBootApplication
public class PushSqlSpringbootApplication implements CommandLineRunner {
    @Autowired
    private UpdatePushSqlService updatePushSqlService;

    @Value("${myProp.excelFile}")
    private String FILE_PATH ;

    @Value("${myProp.oracle.userName}")
    private String USER_NAME ;
    @Value("${myProp.oracle.password}")
    private String PASSWORD ;


    public static void main(String[] args) {
        SpringApplication.run(PushSqlSpringbootApplication.class, args);
    }

    private Logger logger = LoggerFactory.getLogger("pushSql");

    @Override
    public void run(String... args) {
        logger.info("====================开始执行推送sql程序====================");
// 一 加载数据库名文件
        String filePath = FILE_PATH; // 数据库地址全路径
        logger.info("读取文件路径：{}",filePath);
        InputStream fis ;
        Workbook workbook = null;
        try {
            fis = new FileInputStream(filePath);
            if (filePath.endsWith(".xlsx")) {
                workbook = new XSSFWorkbook(fis);
            } else if (filePath.endsWith(".xls") || filePath.endsWith(".et")) {
                workbook = new HSSFWorkbook(fis);
            }
            fis.close();
        } catch (FileNotFoundException e) {
            logger.info("文件不存在，检查路径是否正确：{}",filePath);
            e.printStackTrace();
        } catch (IOException e) {
            logger.info("excel文件错误，检查文件：{}",filePath);
            e.printStackTrace();
        }

        Sheet sheet = workbook.getSheetAt(0);

        // 获取行
        Iterator<Row> rows = sheet.rowIterator();
        Row row;
        Cell cell;

        String userName = USER_NAME;
        String password = PASSWORD;
        int i =0;//读取到的行数
        while (rows.hasNext()) {
            ++i;
            logger.info("++开始读取第 {} 行",i);

            row = rows.next();
            // 获取单元格
            Cell cell0 = row.getCell(0);// 路径地址
            String cellValue = cell0.getStringCellValue();
            //  1
            // 链接数据库 执行sql
            try{
                updatePushSqlService.executeSql(cellValue,userName,password);
            }catch (Exception e){
                logger.warn("******读取第 {} 行出现错误，跳过！{}",i,e);
            }
            logger.info("++结束读取第 {} 行",i);
        }
        logger.info("====================结束执行推送sql程序====================");
    }
}
