package id.co.lcs.apps.utils;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;


/**
 * Created by Samuel Gunawan on 11/29/2017.
 */

public class AnnotationExclusionStrategy implements ExclusionStrategy {

    @Override
    public boolean shouldSkipField(FieldAttributes f) {
        return f.getAnnotation(ApiExclude.class) != null;
    }

    @Override
    public boolean shouldSkipClass(Class<?> clazz) {
        return false;
    }
}