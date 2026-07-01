package de.espend.idea.php.phpunit.ide

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.intellij.ide.DataManager
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.application.ApplicationInfo
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.diagnostic.ErrorReportSubmitter
import com.intellij.openapi.diagnostic.IdeaLoggingEvent
import com.intellij.openapi.diagnostic.SubmittedReportInfo
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.Task
import com.intellij.openapi.ui.Messages
import com.intellij.util.Consumer
import org.apache.commons.lang3.StringUtils
import org.apache.http.NameValuePair
import org.apache.http.client.methods.HttpPost
import org.apache.http.client.utils.URLEncodedUtils
import org.apache.http.entity.StringEntity
import org.apache.http.impl.client.HttpClientBuilder
import org.apache.http.message.BasicNameValuePair
import java.awt.Component
import java.util.ArrayList

/**
 * @author Daniel Espendiller <daniel@espendiller.net>
 */
class PluginErrorReporterSubmitter : ErrorReportSubmitter() {
    override fun submit(
        events: Array<out IdeaLoggingEvent>,
        additionalInfo: String?,
        parentComponent: Component,
        consumer: Consumer<in SubmittedReportInfo>
    ): Boolean {
        val context = DataManager.getInstance().getDataContext(parentComponent)
        val project = CommonDataKeys.PROJECT.getData(context)

        object : Task.Backgroundable(project, "Sending Error Report") {
            override fun run(indicator: ProgressIndicator) {
                val jsonObject = JsonObject()

                val pluginDescriptor = this@PluginErrorReporterSubmitter.pluginDescriptor!!

                val pluginId = pluginDescriptor.pluginId.toString()
                val pluginVersion = pluginDescriptor.version

                jsonObject.addProperty("plugin_id", pluginId)
                jsonObject.addProperty("plugin_version", pluginVersion)

                if (StringUtils.isNotBlank(additionalInfo)) {
                    jsonObject.addProperty("comment", additionalInfo)
                }

                val ide = JsonObject()
                val applicationInfo = ApplicationInfo.getInstance()
                ide.addProperty("version", applicationInfo.build.withoutProductCode().asString())
                ide.addProperty("full_version", applicationInfo.fullVersion)
                ide.addProperty("build", applicationInfo.build.toString())
                jsonObject.add("ide", ide)

                val jsonElements = JsonArray()

                for (event in events) {
                    val jsonEvent = JsonObject()
                    jsonEvent.addProperty("message", event.message)
                    jsonEvent.addProperty("stacktrace", event.throwableText)

                    val throwable = event.throwable
                    if (throwable != null) {
                        jsonEvent.addProperty("stacktrace_message", throwable.message)
                    }

                    jsonElements.add(jsonEvent)
                }

                if (!jsonElements.isEmpty()) {
                    jsonObject.add("events", jsonElements)
                }

                val s = jsonObject.toString()

                ApplicationManager.getApplication().invokeLater {
                    val httpClient = HttpClientBuilder.create().build()

                    var success = false
                    try {
                        val nameValuePairs = ArrayList<NameValuePair>().apply {
                            add(BasicNameValuePair("plugin", pluginId))
                        }

                        val request = HttpPost(
                            "https://espend.de/report-submitter?" + URLEncodedUtils.format(nameValuePairs, "utf-8")
                        )
                        request.addHeader("content-type", "application/json")
                        request.addHeader("x-plugin-version", pluginVersion)

                        request.entity = StringEntity(s)
                        val execute = httpClient.execute(request)
                        httpClient.close()

                        val statusCode = execute.statusLine.statusCode
                        success = statusCode >= 200 && statusCode < 300
                    } catch (ignored: Exception) {
                    }

                    if (!success) {
                        Messages.showErrorDialog(parentComponent, "Failed submitting your report!", "Error Report")
                        return@invokeLater
                    }

                    Messages.showInfoMessage(parentComponent, "Thank you for submitting your report!", "Error Report")
                    consumer.consume(SubmittedReportInfo(SubmittedReportInfo.SubmissionStatus.NEW_ISSUE))
                }
            }
        }.queue()

        return true
    }

    override fun getReportActionText(): String {
        return "Report to espend.de"
    }
}
