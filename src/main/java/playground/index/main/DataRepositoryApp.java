package playground.index.main;


import playground.index.api.DataIndex;
import playground.index.api.RowCallback;
import playground.index.api.ValueExtractor;
import playground.index.impl.ArrayListDataRepository;
import playground.index.impl.ContainsDataIndex;
import playground.index.impl.HashDataIndex;
import playground.index.impl.TreeDataIndex;

import java.util.Arrays;
import java.util.Date;
import java.util.function.Function;

public class DataRepositoryApp {

    public static void main(String... args) {

        DataIndex<PageHit> browserIndex = new HashDataIndex<>(makeIndexValueExtractor("browser", row -> row.browser));
        DataIndex<PageHit> dateIndex = new TreeDataIndex<>(makeIndexValueExtractor("date", row -> row.hitTime));
        DataIndex<PageHit> containsBrowser = new ContainsDataIndex<>(makeIndexValueExtractor("containsbrowser", row -> row.browser));

        ArrayListDataRepository<PageHit> pageHitRepository = new ArrayListDataRepository<>(Arrays.asList(browserIndex, dateIndex, containsBrowser));

        pageHitRepository.insert(new PageHit("www.google.com", new Date(System.currentTimeMillis()), "Singapore", "Chrome 44", "Windows8", "laptop"));
        pageHitRepository.insert(new PageHit("www.yahoo.com", new Date(System.currentTimeMillis()), "Singapore", "IE 11", "Windows8", "laptop"));
        Date start = new Date();
        pageHitRepository.insert(new PageHit("www.wikipedia.org", new Date(System.currentTimeMillis()), "Singapore", "Chrome 40", "Windows8", "laptop"));

        pageHitRepository.query("browser", "Chrome 44", makeDefaultRowCallBack("browser"));
        TreeDataIndex.Range r = new TreeDataIndex.Range(start, new Date());
        pageHitRepository.query("date", r, makeDefaultRowCallBack("data"));

        pageHitRepository.query("containsbrowser", "Chrome", makeDefaultRowCallBack("containsbrowser"));


    }

    private static RowCallback<PageHit> makeDefaultRowCallBack(String indexName) {
        return new RowCallback<PageHit>() {
            @Override
            public void onRow(int rowId, PageHit row) {
                System.out.println(String.format("[%s] RowId %s Page %s", indexName, rowId, row.url));
            }

            @Override
            public void onComplete(int recordCount) {
                System.out.println(String.format("[%s] Search return %s records", indexName, recordCount));
            }
        };
    }

    private static ValueExtractor<PageHit> makeIndexValueExtractor(String indexName, Function<PageHit, Object> extractFunction) {
        return new ValueExtractor<PageHit>() {
            @Override
            public String columnName() {
                return indexName;
            }

            @Override
            public Object extract(PageHit data) {
                return extractFunction.apply(data);
            }
        };
    }


    static class PageHit {
        String url;
        Date hitTime;
        String location;
        String browser;
        String os;
        String device;

        public PageHit(String url, Date hitTime, String location, String browser, String os, String device) {
            this.url = url;
            this.hitTime = hitTime;
            this.location = location;
            this.browser = browser;
            this.os = os;
            this.device = device;
        }
    }
}
