package baubles.api.annotations;

import java.lang.annotation.*;

/**
 * Indicates a part of code that can be removed in any update, including minor updates.
 **/
@Retention(RetentionPolicy.SOURCE)
@Target({
        ElementType.ANNOTATION_TYPE,
        ElementType.CONSTRUCTOR,
        ElementType.FIELD,
        ElementType.METHOD,
        ElementType.PACKAGE,
        ElementType.TYPE
})
@Documented
public @interface UnstableApi {
}
