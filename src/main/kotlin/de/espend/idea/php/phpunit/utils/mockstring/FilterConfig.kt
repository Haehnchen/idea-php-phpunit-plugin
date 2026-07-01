package de.espend.idea.php.phpunit.utils.mockstring

class FilterConfig {
    private val config: MutableMap<String, Item>

    constructor() {
        config = HashMap()
    }

    constructor(filterConfig: FilterConfig) {
        config = HashMap(filterConfig.config)
    }

    fun add(filterConfigItem: Item): FilterConfig {
        val hash = createHash(filterConfigItem.className, filterConfigItem.methodName)
        config[hash] = filterConfigItem
        return this
    }

    fun getItem(className: String, methodName: String): Item? {
        val hash = createHash(className, methodName)
        return config[hash]
    }

    fun getItem(methodName: String): Item? {
        val hash = createHash("", methodName)
        for ((key, value) in config) {
            if (key.endsWith(hash)) {
                return value
            }
        }

        return null
    }

    data class Item(
        val className: String,
        val methodName: String,
        val parameterNumber: Int,
        val filterClass: Class<out Filter>
    )

    companion object {
        private fun createHash(className: String, methodName: String): String {
            return "$className::$methodName"
        }
    }
}
