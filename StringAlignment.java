import java.text.FieldPosition;
import java.text.Format;
import java.text.ParsePosition;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

public class StringAlignment extends Format {

    private static final long serialVersionUID = 1L;

    public enum Alignment {
        LEFT, CENTER, RIGHT,
    }

    /** Current justification for formatting */
    private Alignment currentAlignment;

    /** Current max length of a line */
    private int maxChars;

    public StringAlignment(int maxChars, Alignment align) {
        switch (align) {
            case LEFT, CENTER, RIGHT -> this.currentAlignment = align;
            default -> throw new IllegalArgumentException("invalid justification arg.");
        }
        if (maxChars < 0) {
            throw new IllegalArgumentException("maxChars must be positive.");
        }
        this.maxChars = maxChars;
    }

    public StringBuffer format(Object input, StringBuffer where, FieldPosition ignore)
    {
        String s = input.toString();
        List<String> strings = splitInputString(s);
        ListIterator<String> listItr = strings.listIterator();

        while (listItr.hasNext())
        {
            String wanted = listItr.next();

            //Get the spaces in the right place.
            switch (currentAlignment) {
                case RIGHT -> {
                    pad(where, maxChars - wanted.length());
                    where.append(wanted);
                }
                case CENTER -> {
                    int toAdd = maxChars - wanted.length();
                    pad(where, toAdd / 2);
                    where.append(wanted);
                    pad(where, toAdd - toAdd / 2);
                }
                case LEFT -> {
                    where.append(wanted);
                    pad(where, maxChars - wanted.length());
                }
            }
            where.append("\n");
        }
        return where;
    }

    protected final void pad(StringBuffer to, int howMany) {
        for (int i = 0; i < howMany; i++) {
            to.append(' ');
        }
    }

    String format(String s) {
        return format(s, new StringBuffer(), null).toString();
    }

    /** ParseObject is required, but not useful here. */
    public Object parseObject(String source, ParsePosition pos) {
        return source;
    }

    private List<String> splitInputString(String str) {
        List<String> list = new ArrayList<String>();
        if (str == null)
            return list;
        for (int i = 0; i < str.length(); i = i + maxChars)
        {
            int endindex = Math.min(i + maxChars, str.length());
            list.add(str.substring(i, endindex));
        }
        return list;
    }
}