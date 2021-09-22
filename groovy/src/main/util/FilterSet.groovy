package util

import java.util.regex.Matcher
import java.util.regex.Pattern

/**
 * A filter set
 */
public class FilterSet {
    /**
     * Names to keep.
     */
    public List<Pattern> names2Keep = null

    /**
     * Names to skip.
     */
    public List<Pattern> names2Skip = null

    /**
     * Constructor
     * @param names2Keep
     * @param names2Skip
     */
    public FilterSet(List<Pattern> names2Keep,
                     List<Pattern> names2Skip) {
        this.names2Keep = names2Keep
        this.names2Skip = names2Skip
    }

    /**
     * Tests whether to filter out unwanted metadata using the defined names to keep and skip.
     * Metadata components are filtered out in case the string to filter matches to any entry of the
     * keep list and not to match any entry of the skip list.
     * If the keep list is null or empty it is treated as "matches".
     * If the skip list is null or empty it is treated as "does not match".
     * As a result, if both lists are null, the component is not filtered out.
     * @param toFilter
     * @return true o filter out, else false
     */
    public Boolean filter(String toFilter) {

        //println "Filter with " + names2Keep + "/" + names2Skip

        Boolean keep = ( null == names2Keep || ( 0 == names2Keep.size() ) ) ? true  : matches(names2Keep, toFilter)
        Boolean skip = ( null == names2Skip || ( 0 == names2Skip.size() ) ) ? false : matches(names2Skip, toFilter)

        Boolean filter =  ! (keep && ( ! skip ))
        //println "Filter Result for " + toFilter + ": " + keep + "/" + skip + "=" + filter

        return filter
    }

    /**
     * Tests whether to string matches to any of the patterns.
     * @param patterns
     * @param toFilter
     * @return
     */
    private Boolean matches(List<Pattern> patterns, String toFilter) {
        Boolean matches = false
        for ( Pattern pattern : patterns ) {
            Matcher matcher = pattern.matcher(toFilter)
            if ( matcher.matches() ){
                matches = true
                break
            }
        }
        return matches
    }
}
