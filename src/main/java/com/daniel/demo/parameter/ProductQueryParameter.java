package com.daniel.demo.parameter;

public class ProductQueryParameter {
    private String keyword;
    private String orderBy;
    private String sortRule;
    private Integer priceFrom;
    private Integer priceTo;

    public ProductQueryParameter(){
    }

    public String getKeyword(){
        return keyword;
    }

    public void setKeyword(String keyword){
        this.keyword = keyword;
    }

    public String getOrderBy(){
        return orderBy;
    }

    public void setOrderBy(String orderBy){
        this.orderBy = orderBy;
    }

    public String getSortRule(){
        return sortRule;
    }

    public void setSortRule(String sortRule){
        this.sortRule = sortRule;
    }

    public Integer getPriceFrom() {
        return priceFrom;
    }

    public void setPriceFrom(Integer priceFrom) {
        this.priceFrom = priceFrom;
    }

    public Integer getPriceTo() {
        return priceTo;
    }

    public void setPriceTo(Integer priceTo) {
        this.priceTo = priceTo;
    }


}
