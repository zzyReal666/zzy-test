package spi.typed;

import java.util.Collection;
import java.util.Collections;

/**
 * @author zzypersonally@gmail.com
 * @description
 * @since 2024/3/20 15:39
 */
public interface TypedSPI {

    default String getType() {
        return "";
    }

    default Collection<String> getTypeAliases() {
        return Collections.emptyList();
    }
}
