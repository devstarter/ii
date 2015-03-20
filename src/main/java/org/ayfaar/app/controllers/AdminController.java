package org.ayfaar.app.controllers;

import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.ayfaar.app.utils.TermsMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import static org.ayfaar.app.utils.StringUtils.trim;

@Controller
@RequestMapping("api/admin")
public class AdminController {

    @Autowired TermsMap termsMap;

    @SuppressWarnings("UnusedAssignment")
    @RequestMapping(value = "export-terms")
    @ResponseBody
    public void exportTerms(HttpServletResponse response) throws IOException {
        response.setContentType("application/vnd.ms-excel; charset=utf-8");
        response.setHeader("Content-Disposition", "attachment; filename=\"ii-terms.xlsx\"");

        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("Термины Ииссиидиологии");

        Set<TermsMap.TermProvider> primeTerms = new LinkedHashSet<TermsMap.TermProvider>();

        for (Map.Entry<String, TermsMap.TermProvider> entry : termsMap.getAll()) {
            final TermsMap.TermProvider mainTerm = entry.getValue().getMainTermProvider();
            if (mainTerm != null) primeTerms.add(mainTerm);
        }

        int rownum = 0;
        XSSFRow row = sheet.createRow(rownum++);
        int cellnum = 0;
        row.createCell(cellnum++).setCellValue("Название");
        row.createCell(cellnum++).setCellValue("ЗКК");
        row.createCell(cellnum++).setCellValue("Синонимы");
        row.createCell(cellnum++).setCellValue("Сокращения");
        row.createCell(cellnum++).setCellValue("Короткое описание");
        row.createCell(cellnum++).setCellValue("Описание");

        for (TermsMap.TermProvider t : primeTerms) {
            row = sheet.createRow(rownum++);
            cellnum = 0;
            row.createCell(cellnum++).setCellValue(t.getName());;
            row.createCell(cellnum++).setCellValue(t.hasCode() ? t.getCode().getName() : "");

            String aliases = "";
            for (TermsMap.TermProvider alias : t.getAliases()) {
                aliases += alias.getName() + ", ";
            }
            row.createCell(cellnum++).setCellValue(trim(aliases, ", "));

            String abbrs = "";
            for (TermsMap.TermProvider abbr : t.getAbbreviations()) {
                abbrs += abbr.getName() + ", ";
            }
            row.createCell(cellnum++).setCellValue(trim(abbrs, ", "));

            row.createCell(cellnum++).setCellValue(t.getTerm().getShortDescription());
            row.createCell(cellnum++).setCellValue(t.getTerm().getDescription());
        }

        workbook.write(new BufferedOutputStream(response.getOutputStream()));
    }
}
