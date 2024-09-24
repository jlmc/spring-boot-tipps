package io.github.jlmc.poc.st;

import org.hamcrest.CustomMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.json.JSONException;

import static org.skyscreamer.jsonassert.JSONAssert.assertEquals;

public class JsonMatches {

    static Matcher<String> jsonEqualsTo(final String expected) {
        return jsonEqualsTo(expected, false);
    }

    public static Matcher<String> jsonEqualsTo(final String expected,  final boolean strict) {
        return new CustomMatcher<>("Json is Equals to") {

            String assertMatcherError;

            @Override
            public void describeMismatch(Object item, Description description) {
                if (assertMatcherError != null) {
                    description.appendText("was ").appendValue(assertMatcherError);
                } else {
                    super.describeMismatch(item, description);
                }
            }

            @Override
            public boolean matches(Object actual) {
                if (actual instanceof String given) {
                    return isEquals(given);

                }
                return false;
            }

            private boolean isEquals(String given) {
                try {
                    assertEquals(expected, given, strict);
                    return true;
                } catch (AssertionError e) {
                    assertMatcherError = e.getMessage();
                    return false;
                } catch (JSONException e) {
                    System.out.println("UNEXPECTED JSON EXCEPTION: " + e.getMessage());
                    throw new RuntimeException(e);
                }
            }
        };
    }

}
