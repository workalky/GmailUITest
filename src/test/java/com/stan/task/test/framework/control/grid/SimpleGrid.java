
package com.stan.task.test.framework.control.grid;

import java.util.HashMap;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.stan.task.test.framework.control.Element;
import com.stan.task.test.framework.control.ElementLocator;
import com.stan.task.test.framework.control.HtmlDoc;
import com.stan.task.test.framework.datastructures.DataTable;
import com.stan.task.test.framework.exception.ControlLayerException;
import com.stan.task.test.framework.page.AbstractPage;
import com.stan.task.test.framework.utils.ScrollUtils;
import com.stan.task.test.framework.utils.WebElementHelper;

/**
 * The SimpleGrid Class: no header, no checkboxes, no paging - just grid. Base class for many other grids
 */
public class SimpleGrid extends Element // implements Grid
{
    protected static final String XPATH_CHILD_PREFIX = ".";

    private static final String ROWS_WITH_LINK_EQUALS_TEXT_XPATH = XPATH_CHILD_PREFIX + "//tr[td//a[text()='%s']]";
    private static final String ROW_COLUMNTEXTWITHICON_XPATH = XPATH_CHILD_PREFIX + "//tr/td/div[contains(text(),'%s')]/../../td/div[contains(@class, 'x-grid-cell-inner')]//label[text()='%s']";

    protected static final String ROW_CONTAINING_ANY_CELL_WITH_EXACT_TEXT_XPATH = XPATH_CHILD_PREFIX + "//tr[td/div[contains(@class, 'x-grid-cell-inner') and text()='%s']]";

    protected static final String ROW_CONTAINING_ANY_CELL_WITH_ELEMENT_WITH_EXACT_TEXT_XPATH = XPATH_CHILD_PREFIX + "//tr[td/div[text()='%s']]";

    protected static final String ROW_CONTAINING_ANY_CELL_WITH_LINK_WITH_EXACT_TEXT_XPATH = XPATH_CHILD_PREFIX + "//tr[td/div[contains(@class, 'x-grid-cell-inner')]/a[text()='%s']]";

    protected static final String GRID_VIEW_PANEL_CSS = "div.x-grid-view";
    protected static final String TABLE_CSS = GRID_VIEW_PANEL_CSS + " table.x-grid-table";

    private static final String CELL_WITH_COLUMN_ORDINAL_IN_ROW_WITH_EXACT_TEXT_CSS = "tr:has(**td > div:text(**%s**)**) td:nth-child(%s)";
    private static final String CELL_WITH_COLUMN_ORDINAL_IN_ROW_WITH_PARTIAL_TEXT_CSS = "tr:has(**td > div:contains(**%s**)**) td:nth-child(%s)";

    private static final String DIRTY_CELL_CSS = ".x-grid-dirty-cell";

    private static final String CELL_BY_CONTAINER_ROW_AND_COLUMN_ORDINAL_XPATH = XPATH_CHILD_PREFIX + "//tr[contains(@id, '%s')]//tr[%d]/td[%d]/div[contains(@class, 'x-grid-cell-inner')]";
    private static final String CELL_BY_ROW_AND_COLUMN_ORDINAL_XPATH = XPATH_CHILD_PREFIX + "//tr[%d]/td[%d]/div[contains(@class, 'x-grid-cell-inner')]";
    private static final String CELL_BY_COLUMN_ORDINAL_WITH_EXACT_TEXT_XPATH = XPATH_CHILD_PREFIX + "//tr/td[%d]/div[contains(@class, 'x-grid-cell-inner') and text()='%s']";

    public static final String QUERY_ROWS_CSS = "div#feMain2 .local_table tr:not(.sum)";
    public static final String SUBQUERY_CELLS_CSS = " td";

    protected static final String COLUMN_HEADERS_TEXT_CSS = ".x-column-header-text";

    public SimpleGrid(AbstractPage parentBrowserItem, ElementLocator elementLocator, String fieldControlName)
    {
        super(parentBrowserItem, elementLocator, fieldControlName);
    }

    public SimpleGrid(AbstractPage parentBrowserItem, List<ElementLocator> elementLocators, String fieldControlName)
    {
        super(parentBrowserItem, elementLocators, fieldControlName);
    }

    public SimpleGrid(AbstractPage parentBrowserItem, WebElement element, String controlName)
    {
        super(parentBrowserItem, element, controlName);
    }

    /**
     * return true if the grid has a row with the exact text in the column with the specified 1-based ordinal
     * 
     * @param columnOrdinal
     *        1-based column ordinal
     * @param exactCellText
     *        exact cell text to match
     * @return return true if the grid has a row with the exact text in the column with the specified 1-based ordinal
     */
    public boolean hasRowWithTextInColumn(int columnOrdinal, String exactCellText)
    {
        String locator = String.format(CELL_BY_COLUMN_ORDINAL_WITH_EXACT_TEXT_XPATH, columnOrdinal, exactCellText);
        WebElement row = findChildSeleniumWebElement(By.xpath(locator));
        return row != null;
    }

    /**
     * Selects a row containing the specified text in the specified column.
     * 
     * @param columnOrdinal
     *        the 1-based column ordinal of the column to search for the text
     * @param exactText
     *        the text to find
     */
    public void selectRowWithTextInColumn(int columnOrdinal, String exactText)
    {
        String locator = String.format(CELL_BY_COLUMN_ORDINAL_WITH_EXACT_TEXT_XPATH, columnOrdinal, exactText);
        WebElement row = findChildSeleniumWebElement(By.xpath(locator));
        row.click();

    }

    public int getRowCountWithCellEqualsText(String text)
    {
        List<WebElement> rows = findChildSeleniumWebElements(By.xpath(ROW_CONTAINING_ANY_CELL_WITH_EXACT_TEXT_XPATH.replaceFirst("%s",
            text)));

        int size = 0;

        if (rows != null)
        {
            size = rows.size();
        }
        return size;
    }

    public int getRowCountWithCellWithLinkEqualsText(String text)
    {
        List<WebElement> rows = findChildSeleniumWebElements(By.xpath(ROW_CONTAINING_ANY_CELL_WITH_LINK_WITH_EXACT_TEXT_XPATH
            .replaceFirst("%s", text)));

        int size = 0;

        if (rows != null)
        {
            size = rows.size();
        }
        return size;
    }

    public boolean isIncorrectlyFilledCellPresent()
    {
        WebElement tableElement = findChildSeleniumWebElement(By.cssSelector(DIRTY_CELL_CSS));
        return tableElement != null;
    }

    /**
     * Gets text of cell by its 1-based row and column ordinals
     * 
     * @param rowOrdinal
     *        1-based row ordinal
     * @param columnOrdinal
     *        1-based column ordinal
     * 
     * @return cell text
     */
    public String getCellText(int rowOrdinal, int columnOrdinal)
    {
        try
        {
            String filledInXpath = String.format(CELL_BY_ROW_AND_COLUMN_ORDINAL_XPATH, rowOrdinal, columnOrdinal);
            WebElement cell = findChildSeleniumWebElement(By.xpath(filledInXpath));

            return cell.getText();
        }
        catch (Throwable exception)
        {
            throw new ControlLayerException(
                "Could not get cell text with row ordinal " + rowOrdinal
                    + " and column ordinal " + columnOrdinal,
                exception);
        }
    }

    /**
     * Gets the cell text by container name and its 1-based row and column ordinals
     * 
     * @param container
     *        - container name
     * @param rowOrdinal
     *        1-based row ordinal
     * @param columnOrdinal
     *        1-based column ordinal
     * @return cell text
     */
    public String getCellText(String container, int rowOrdinal, int columnOrdinal)
    {
        String filledInXpath = String.format(CELL_BY_CONTAINER_ROW_AND_COLUMN_ORDINAL_XPATH,
            container,
            rowOrdinal,
            columnOrdinal);
        WebElement cell = findChildSeleniumWebElement(By.xpath(filledInXpath));
        return cell.getText();
    }



    // /**
    // * Return 1-based row ordinal of the row which contains a cell with the exact text specified
    // *
    // * @param exactCellText
    // * - text to find
    // *
    // * @return row number, -1 in case there is no row with such text
    // */
    // public int getRowOrdinalOfRowContainingCellWithText(String exactCellText)
    // {
    // // Due to several hidden rows, the effective row index is offset by 2 from the actual row index
    // int effectiveRowIndex = -1;
    //
    // if (getRowCountWithCellEqualsText(exactCellText) != 0)
    // {
    // List<WebElement> gridRows = getRowSeleniumWebElements();
    //
    // for (int i = 0; i < gridRows.size(); i++)
    // {
    // String[] columnValues = gridRows.get(i).getText().split("\n");
    //
    // for (String columnValue : columnValues)
    // {
    // if (exactCellText.equals(columnValue.trim()))
    // {
    // effectiveRowIndex = i + 2;
    // break;
    // }
    // }
    // }
    // }
    //
    // return effectiveRowIndex;
    //
    // }

    public boolean hasScroll()
    {
        try
        {
            WebElement scrollableElement = getScrollableElement();

            return ScrollUtils.hasScroll(scrollableElement);
        }
        catch (Throwable exception)
        {
            return false;
        }
    }

    public void scrollTo(WebElement scrollToElement)
    {
        if (!hasScroll())
        {
            throw new ControlLayerException("Grid doesn't have scroll");
        }
        WebElement scrollableElement = getScrollableElement();
        ScrollUtils.scrollTo(scrollToElement, scrollableElement);
    }

    /**
     * Scrolls the content of the grid to the left.
     * 
     * @param pixels
     *        the number of pixels by which the contents is scrolled to the left respective to the beginning.
     */
    public void scrollLeftIfScrollable(String pixels)
    {
        try
        {
            WebElement scrollableElement = getScrollableElement();
            WebElementHelper.setScrollLeft(scrollableElement, pixels);
        }
        catch (Throwable e)
        {
            return;
        }
    }

    /**
     * Scrolls the content to the left to its maximum value.
     */
    public void scrollToLeft()
    {
        int maxValue = ScrollUtils.getLeftMaximunScrollValue(getScrollableElement());
        scrollLeftIfScrollable(String.valueOf(maxValue));
    }

    /**
     * Scroll to this element if it is scrollable
     * 
     * @param elementToScrollTo
     */
    public void scrollToIfScrollable(WebElement elementToScrollTo)
    {
        try
        {
            WebElement scrollableElement = getScrollableElement();

            ScrollUtils.scrollTo(elementToScrollTo, scrollableElement);
        }
        catch (Throwable exception)
        {
            return;
        }
    }

    private WebElement getScrollableElement()
    {
        WebElement scrollableElement = getSeleniumWebElement(true).findElement(By.cssSelector(GRID_VIEW_PANEL_CSS));
        return scrollableElement;
    }

    protected List<String> getColumnHeaders() // String cssSelector)
    {
        // ArrayList<String> allColumnHeaders = new ArrayList<String>();
        //
        // String columnHeader;
        //
        // scrollToRight();
        //
        //
        // for (int iteration = 0; iteration < 2; iteration++)
        // {
        // if (iteration == 1)
        // {
        // scrollToLeft();
        //
        // }
        //
        // for (WebElement columnHeaderWebElement : getSeleniumWebElement(true).findElements(By.cssSelector(cssSelector)))
        // {
        // columnHeader = GridHtmlDoc.cleanHtmlText(columnHeaderWebElement.getText());
        //
        // if ((iteration == 0 || !allColumnHeaders.contains(columnHeader)) && !columnHeader.equals(""))
        // {
        // allColumnHeaders.add(columnHeader);
        // }
        // }
        //
        // }
        //
        // scrollToRight();
        //
        // return allColumnHeaders;
        return null;
    }


    public void scrollToRight()
    {
        try
        {
            WebElement scrollableElement = getScrollableElement();
            WebElementHelper.setScrollLeft(scrollableElement, "0");
        }
        catch (Throwable e)
        {
            return;
        }
    }

    public boolean hasRowWithIconContainingColumnValue(String textRowIdentifier, String value)
    {
        String locator = String.format(ROW_COLUMNTEXTWITHICON_XPATH,
            textRowIdentifier,
            value);
        WebElement row = findChildSeleniumWebElement(By.xpath(locator));
        return row != null;
    }

    public int getRowCountWithCellEqualsLinkText(String exactLinkCellText)
    {
        String locator = String.format(ROWS_WITH_LINK_EQUALS_TEXT_XPATH, exactLinkCellText);

        List<WebElement> rows = findChildSeleniumWebElements(By.xpath(locator));

        int size = 0;

        if (rows != null)
        {
            size = rows.size();
        }
        return size;
    }

    public int getRowCount()
    {
        return 0;
    }

    public DataTable<String> getTable(String rowsQueryCss)
    {
        DataTable<String> table = new DataTable<String>("Table Data");
        int colIndex;

        for (WebElement rowElement : GetElementsByCSS(rowsQueryCss))
        {
            HashMap<String, String> newRow = new HashMap<String, String>();

            colIndex = -1;

            for (WebElement cell : rowElement.findElements(By.cssSelector(SUBQUERY_CELLS_CSS)))
            {
                colIndex++;

                table.addColumn(String.valueOf(colIndex), "");

                newRow.put(Integer.toString(colIndex), HtmlDoc.cleanHtmlText(cell.getText()));
            }

            table.addRow(newRow);
        }

        return table;
    }
}