package com.elderbyte.vidada.dto;

import com.elderbyte.vidada.tags.Tag;

/**
 * Created by isnull on 21/03/16.
 */
public class TagInfoDto {

    public String name;
    public int usages;


    public TagInfoDto(Tag tag){
        this.name = tag.getName();

    }




}
