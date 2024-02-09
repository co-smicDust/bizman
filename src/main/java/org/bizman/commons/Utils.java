package org.bizman.commons;

import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;

import java.util.*;

public class Utils {
    private static ResourceBundle validationsBundle;
    private static ResourceBundle errorsBundle;

    static {
        validationsBundle = ResourceBundle.getBundle("messages.validations");
        errorsBundle = ResourceBundle.getBundle("messages.errors");
    }

    public static String getMessage(String code, String bundleType) {
        bundleType = Objects.requireNonNullElse(bundleType, "validation");
        ResourceBundle bundle = bundleType.equals("error")? errorsBundle:validationsBundle;
        try {
            return bundle.getString(code);
        } catch (Exception e) {
            return null;
        }
    }

    public static Map<String, List<String>> getMessages(Errors errors){
        try {
            Map<String, List<String>> data = new HashMap<>();   //아래에서 뽑은 데이터 담는 곳
            for (FieldError error : errors.getFieldErrors()) { //필드별로 에러 뽑아오기
                String field = error.getField();
                List<String> messages = Arrays.stream(error.getCodes()).sorted(Comparator.reverseOrder())   // 코드를 가져와서 정렬
                        .map(c -> getMessage(c, "validation"))  // 조회
                        .filter(c -> c != null) // 존재하는 것만
                        .toList();  // List로 가공
                data.put(field, messages);
            }
            return data;
        } catch (Exception e) {
            return null;
        }
    }
}
