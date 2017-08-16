/**
 * 
 */
package com.perago.techtest.util;

import java.util.Objects;

/**
 * @author Jabu
 *
 */
public final class StringUtils {
    private StringUtils() {
    }

    public static boolean areStringEqual(String str1, String str2) {
	if (str1 == str2)
	    return true;
	else if (Objects.isNull(str1) && Objects.isNull(str2))
	    return true;
	else if (Objects.isNull(str1) && !Objects.isNull(str2))
	    return false;
	else if (!Objects.isNull(str1) && Objects.isNull(str2))
	    return false;
	else
	    return str1.equals(str2);

    }
}
