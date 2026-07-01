package de.espend.idea.php.phpunit

import com.intellij.openapi.util.IconLoader
import javax.swing.Icon

/**
 * @author Daniel Espendiller <daniel@espendiller.net>
 */
class PhpUnitIcons {
    companion object {
        @JvmField
        val PHPUNIT: Icon = IconLoader.getIcon("/icons/phpunit.svg", PhpUnitIcons::class.java)
    }
}
