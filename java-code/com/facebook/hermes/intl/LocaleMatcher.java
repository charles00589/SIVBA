package com.facebook.hermes.intl;

import android.icu.util.ULocale;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;

/* loaded from: classes.dex */
public class LocaleMatcher {

    /* loaded from: classes.dex */
    public static class LocaleMatchResult {
        public HashMap<String, String> extensions = new HashMap<>();
        public ILocaleObject<?> matchedLocale;
    }

    public static String BestAvailableLocale(String[] strArr, String str) {
        while (Arrays.asList(strArr).indexOf(str) <= -1) {
            int lastIndexOf = str.lastIndexOf("-");
            if (lastIndexOf < 0) {
                return "";
            }
            if (lastIndexOf >= 2 && str.charAt(lastIndexOf - 2) == '-') {
                lastIndexOf -= 2;
            }
            str = str.substring(0, lastIndexOf);
        }
        return str;
    }

    public static LocaleMatchResult lookupMatch(String[] strArr, String[] strArr2) throws JSRangeErrorException {
        LocaleMatchResult localeMatchResult = new LocaleMatchResult();
        for (String str : strArr) {
            ILocaleObject createFromLocaleId = LocaleObject.createFromLocaleId(str);
            String BestAvailableLocale = BestAvailableLocale(strArr2, createFromLocaleId.toCanonicalTagWithoutExtensions());
            if (!BestAvailableLocale.isEmpty()) {
                localeMatchResult.matchedLocale = LocaleObject.createFromLocaleId(BestAvailableLocale);
                localeMatchResult.extensions = createFromLocaleId.getUnicodeExtensions();
                return localeMatchResult;
            }
        }
        localeMatchResult.matchedLocale = LocaleObject.createDefault();
        return localeMatchResult;
    }

    public static String[] getAvailableLocales() {
        ArrayList arrayList = new ArrayList();
        for (Locale locale : Locale.getAvailableLocales()) {
            arrayList.add(locale.toLanguageTag());
        }
        return (String[]) arrayList.toArray(new String[arrayList.size()]);
    }

    public static LocaleMatchResult lookupMatch(String[] strArr) throws JSRangeErrorException {
        return lookupMatch(strArr, getAvailableLocales());
    }

    public static String[] lookupSupportedLocales(String[] strArr) throws JSRangeErrorException {
        ArrayList arrayList = new ArrayList();
        String[] availableLocales = getAvailableLocales();
        for (String str : strArr) {
            String BestAvailableLocale = BestAvailableLocale(availableLocales, LocaleObject.createFromLocaleId(str).toCanonicalTagWithoutExtensions());
            if (BestAvailableLocale != null && !BestAvailableLocale.isEmpty()) {
                arrayList.add(str);
            }
        }
        return (String[]) arrayList.toArray(new String[arrayList.size()]);
    }

    public static ULocale bestFitBestAvailableLocale(ILocaleObject<?> iLocaleObject) throws JSRangeErrorException {
        ULocale[] availableLocales = ULocale.getAvailableLocales();
        ULocale[] uLocaleArr = {(ULocale) iLocaleObject.getLocaleWithoutExtensions()};
        boolean[] zArr = new boolean[1];
        ULocale acceptLanguage = ULocale.acceptLanguage(uLocaleArr, availableLocales, zArr);
        if (zArr[0] || acceptLanguage == null) {
            return null;
        }
        return acceptLanguage;
    }

    public static LocaleMatchResult bestFitMatch(String[] strArr) throws JSRangeErrorException {
        LocaleMatchResult localeMatchResult = new LocaleMatchResult();
        for (String str : strArr) {
            ILocaleObject createFromLocaleId = LocaleObject.createFromLocaleId(str);
            ULocale bestFitBestAvailableLocale = bestFitBestAvailableLocale(createFromLocaleId);
            if (bestFitBestAvailableLocale != null) {
                localeMatchResult.matchedLocale = LocaleObjectICU.createFromULocale(bestFitBestAvailableLocale);
                localeMatchResult.extensions = createFromLocaleId.getUnicodeExtensions();
                return localeMatchResult;
            }
        }
        localeMatchResult.matchedLocale = LocaleObjectICU.createDefault();
        return localeMatchResult;
    }

    public static String[] bestFitSupportedLocales(String[] strArr) throws JSRangeErrorException {
        ArrayList arrayList = new ArrayList();
        for (String str : strArr) {
            if (bestFitBestAvailableLocale(LocaleObject.createFromLocaleId(str)) != null) {
                arrayList.add(str);
            }
        }
        return (String[]) arrayList.toArray(new String[arrayList.size()]);
    }
}
