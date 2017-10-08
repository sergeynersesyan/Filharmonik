package am.apo.filharmonik;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.StyleSpan;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Mr Nersesyan on 29/08/2017.
 */

public class FormatHelper {


    public static CharSequence parse(CharSequence input) {
        Spannable spannable;
        Matcher matcher = BOLD_PATTERN.matcher(input);
        while (matcher.find()) {
//            String text = matcher.group();
//            input.subSequence()
//            int end = matcher.end();
//            if (!url.contains("/") && !url.startsWith("www.")) continue;
//            if (url.endsWith(".")) {
//                url = url.substring(0, url.length() - 1);
//                end--;
//            }
        }
        spannable = new SpannableString(input);
//        spannable.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), matcher.start(), end, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        return spannable;
    }

    private static final Pattern BOLD_PATTERN = Pattern.compile("(\\[b\\])(.*?)(\\[\\/b\\])");
}
