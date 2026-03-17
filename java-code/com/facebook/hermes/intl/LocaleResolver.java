package com.facebook.hermes.intl;

import com.facebook.hermes.intl.LocaleMatcher;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

/* loaded from: classes.dex */
public class LocaleResolver {
    public static HashMap<String, Object> resolveLocale(List<String> list, Object obj, List<String> list2) throws JSRangeErrorException {
        LocaleMatcher.LocaleMatchResult bestFitMatch;
        HashMap<String, Object> hashMap = new HashMap<>();
        if (JSObjects.getJavaString(JSObjects.Get(obj, Constants.LOCALEMATCHER)).equals(Constants.LOCALEMATCHER_LOOKUP)) {
            bestFitMatch = LocaleMatcher.lookupMatch((String[]) list.toArray(new String[list.size()]));
        } else {
            bestFitMatch = LocaleMatcher.bestFitMatch((String[]) list.toArray(new String[list.size()]));
        }
        HashSet<String> hashSet = new HashSet();
        for (String str : list2) {
            Object Null = JSObjects.Null();
            Object obj2 = Null;
            if (!bestFitMatch.extensions.isEmpty()) {
                obj2 = Null;
                if (bestFitMatch.extensions.containsKey(str)) {
                    String str2 = bestFitMatch.extensions.get(str);
                    boolean isEmpty = str2.isEmpty();
                    Object obj3 = str2;
                    if (isEmpty) {
                        obj3 = JSObjects.newString("true");
                    }
                    hashSet.add(str);
                    obj2 = obj3;
                }
            }
            Object obj4 = obj2;
            if (JSObjects.getJavaMap(obj).containsKey(str)) {
                Object Get = JSObjects.Get(obj, str);
                boolean isString = JSObjects.isString(Get);
                Object obj5 = Get;
                if (isString) {
                    boolean isEmpty2 = JSObjects.getJavaString(Get).isEmpty();
                    obj5 = Get;
                    if (isEmpty2) {
                        obj5 = JSObjects.newBoolean(true);
                    }
                }
                obj4 = obj2;
                if (!JSObjects.isUndefined(obj5)) {
                    boolean equals = obj5.equals(obj2);
                    obj4 = obj2;
                    if (!equals) {
                        hashSet.remove(str);
                        obj4 = obj5;
                    }
                }
            }
            boolean isNull = JSObjects.isNull(obj4);
            Object obj6 = obj4;
            if (!isNull) {
                obj6 = UnicodeExtensionKeys.resolveKnownAliases(str, obj4);
            }
            if (JSObjects.isString(obj6) && !UnicodeExtensionKeys.isValidKeyword(str, JSObjects.getJavaString(obj6), bestFitMatch.matchedLocale)) {
                hashMap.put(str, JSObjects.Null());
            } else {
                hashMap.put(str, obj6);
            }
        }
        for (String str3 : hashSet) {
            ArrayList<String> arrayList = new ArrayList<>();
            String javaString = JSObjects.getJavaString(UnicodeExtensionKeys.resolveKnownAliases(str3, JSObjects.newString(bestFitMatch.extensions.get(str3))));
            if (!JSObjects.isString(javaString) || UnicodeExtensionKeys.isValidKeyword(str3, JSObjects.getJavaString(javaString), bestFitMatch.matchedLocale)) {
                arrayList.add(javaString);
                bestFitMatch.matchedLocale.setUnicodeExtensions(str3, arrayList);
            }
        }
        hashMap.put(Constants.LOCALE, bestFitMatch.matchedLocale);
        return hashMap;
    }
}
