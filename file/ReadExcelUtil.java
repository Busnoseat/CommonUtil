package ;

import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.xmlbeans.SimpleValue;
import org.junit.Test;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * The type ReadExcelUtil.
 *
 * @author xubo
 * @Description:
 * @Date 2016/7/19
 */
public class ReadExcelUtil {

    /**
    *<dependency>
    *       <groupId>org.apache.poi</groupId>
    *        <artifactId>poi-ooxml</artifactId>
    *       <version>3.14</version>
    *  </dependency>
    *    <dependency>
    *        <groupId>org.apache.poi</groupId>
    *        <artifactId>poi-ooxml-schemas</artifactId>
    *        <version>3.11</version>
    *    </dependency>
    *    <dependency>
    *        <groupId>org.apache.xmlbeans</groupId>
    *        <artifactId>xmlbeans</artifactId>
    *        <version>2.6.0</version>
    *    </dependency>
    *
    */

    /**
     * read excel2003 and excel 2010 from url
     *
     * @param url
     * @return
     * @author xubo
     */
    public static <T> List<T> readExcelFromUrl(String url, Class<T> tarType) {
        if (url == null || url.length() == 0)
            return null;
        String fName = url.substring(url.lastIndexOf(".")).toUpperCase();
        InputStream is = null;
        try {
            is = new FileInputStream(url);
            if (CommonDicConstant.OFFICE_EXCEL_2003_POSTFIX.equals(fName))
                return readXLS(is, tarType);
            if (CommonDicConstant.OFFICE_EXCEL_2010_POSTFIX.equals(fName))
                return readXLSX(is, tarType);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (IntrospectionException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * read Excel(2003-2007)
     * There is a action  that bean who returns use Double instead of double and the same with
     * Integer and others.
     * @param is
     * @param tarType
     * @param <T>
     * @return
     * @throws IOException
     * @throws IntrospectionException
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    public static <T> List<T> readXLS(InputStream is, Class<T> tarType) throws IOException, IntrospectionException, InvocationTargetException, IllegalAccessException, InstantiationException {
        HSSFWorkbook hwb = new HSSFWorkbook(is);
        HSSFSheet sheet = hwb.getSheetAt(0);
        T t = null;
        List<T> list = new LinkedList<>();
        SimpleValue value = null;
        final Field[] fields = tarType.getDeclaredFields();
        for (int i = 1; i < sheet.getLastRowNum(); i++) {
            t = tarType.newInstance();
            HSSFRow row = sheet.getRow(i);
            Iterator itr = row.iterator();
            while (itr.hasNext()) {
                HSSFCell cell = (HSSFCell) itr.next();
                Field field = fields[cell.getColumnIndex()];
                PropertyDescriptor pd = new PropertyDescriptor(field.getName(), tarType);
                Method m = pd.getWriteMethod();
                if (cell.getCellType() == cell.CELL_TYPE_NUMERIC) {
                    if (HSSFDateUtil.isCellDateFormatted(cell)) {
                        m.invoke(t, cell.getDateCellValue());
                    } else {
                        m.invoke(t, cell.getNumericCellValue());
                    }
                } else if (cell.getCellType() == cell.CELL_TYPE_STRING) {
                    m.invoke(t, cell.getStringCellValue());
                } else if (cell.getCellType() == cell.CELL_TYPE_BLANK) {
                    m.invoke(t, value);
                }
            }
            list.add(t);
        }
        return list;
    }

    /**
     * read Excel(2010)
     * There is a action  that bean who returns use Double instead of double and the same with
     * Integer and others. 
     * @param is
     * @param tarType
     * @param <T>
     * @return
     * @throws IOException
     * @throws IllegalAccessException
     * @throws InstantiationException
     * @throws IntrospectionException
     * @throws InvocationTargetException
     */
    public static <T> List<T> readXLSX(InputStream is, Class<T> tarType) throws IOException, IllegalAccessException, InstantiationException, IntrospectionException, InvocationTargetException {
        XSSFWorkbook workbook = new XSSFWorkbook(is);
        XSSFSheet sheet = workbook.getSheetAt(0);
        T t = null;
        List<T> list = new LinkedList<>();
        final Field[] fields = tarType.getDeclaredFields();
        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            XSSFRow row = sheet.getRow(i);
            t = tarType.newInstance();
            Iterator itr = row.iterator();
            while (itr.hasNext()) {
                XSSFCell cell = (XSSFCell) itr.next();
                Field field = fields[cell.getColumnIndex()];
                PropertyDescriptor pd = new PropertyDescriptor(field.getName(), tarType);
                Method m = pd.getWriteMethod();
                if (cell.getCellType() == cell.CELL_TYPE_NUMERIC) {
                    if (HSSFDateUtil.isCellDateFormatted(cell)) {
                        m.invoke(t, cell.getDateCellValue());
                    } else {
                        m.invoke(t, cell.getNumericCellValue());
                    }
                } else if (cell.getCellType() == cell.CELL_TYPE_STRING) {
                    m.invoke(t, cell.getStringCellValue());
                } else if (cell.getCellType() == cell.CELL_TYPE_BLANK) {
                    m.invoke(t, cell.getRawValue());
                }
            }
            list.add(t);
        }
        return list;
    }

}
