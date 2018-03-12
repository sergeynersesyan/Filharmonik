package am.apo.filharmonik2;

import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.StyleSpan;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Mr Nersesyan on 29/08/2017.
 */

public class FormatParser {

    public static final String TAG_BOLD_OPEN = "[b]";
    public static final String TAG_BOLD_CLOSE = "[/b]";

    public static CharSequence parse(CharSequence input) {
        Matcher matcher = BOLD_PATTERN.matcher(input);
        ArrayList<Mention> mentions = new ArrayList<>();
        int delta = 0;
        while (matcher.find()) {
            String tag = matcher.group();
            String name = getNameFromTag(tag);
            input = input.subSequence(0, matcher.start() - delta) + name + input.subSequence(matcher.end() - delta, input.length());
            mentions.add(new Mention(matcher.start() - delta, name));
            delta += tag.length() - name.length();
        }
        Spannable spannable = new SpannableString(input);
        for (final Mention mention : mentions) {
            spannable.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), mention.start, mention.start + mention.userName.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        return spannable;
    }

    private static class Mention {
        int start;
        String userName;

        Mention(int start, String name) {
            this.start = start;
            userName = name;
        }
    }

    private static String getNameFromTag(String tag) {
        // tag = [user id = "444444"]David[/user]
        int start = tag.indexOf(']')+1;
        int end = tag.indexOf(TAG_BOLD_CLOSE);
        return tag.substring(start, end).trim();
    }
    private static final Pattern BOLD_PATTERN = Pattern.compile("(\\[b\\])(.+?)(\\[\\/b\\])");
}
