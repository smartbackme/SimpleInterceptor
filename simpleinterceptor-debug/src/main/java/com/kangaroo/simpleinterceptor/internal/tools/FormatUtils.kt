package com.kangaroo.simpleinterceptor.internal.tools

import android.content.Context
import android.text.TextUtils
import com.google.gson.JsonParser
import com.kangaroo.simpleinterceptor.R
import com.kangaroo.simpleinterceptor.internal.data.HttpHeader
import com.kangaroo.simpleinterceptor.internal.data.HttpTransaction
import com.kangaroo.simpleinterceptor.internal.tools.JsonConvertor.instance
import org.xml.sax.InputSource
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.util.*
import javax.xml.transform.OutputKeys
import javax.xml.transform.Source
import javax.xml.transform.sax.SAXSource
import javax.xml.transform.sax.SAXTransformerFactory
import javax.xml.transform.stream.StreamResult

internal object FormatUtils {
    @JvmStatic
    fun formatHeaders(httpHeaders: List<HttpHeader>?, withMarkup: Boolean): String {
        var out = ""
        if (httpHeaders != null) {
            for ((name, value) in httpHeaders) {
                out += (if (withMarkup) "<b>" else "") + name + ": " + (if (withMarkup) "</b>" else "") +
                        value + if (withMarkup) "<br />" else "\n"
            }
        }
        return out
    }

    @JvmStatic
    fun formatByteCount(bytes: Long, si: Boolean): String {
        val unit = if (si) 1000 else 1024
        if (bytes < unit) return "$bytes B"
        val exp = (Math.log(bytes.toDouble()) / Math.log(unit.toDouble())).toInt()
        val pre = (if (si) "kMGTPE" else "KMGTPE")[exp - 1].toString() + if (si) "" else "i"
        return String.format(
            Locale.US,
            "%.1f %sB",
            bytes / Math.pow(unit.toDouble(), exp.toDouble()),
            pre
        )
    }

    @JvmStatic
    fun formatJson(json: String?): String? {
        return try {
            val je = JsonParser.parseString(json)
            instance.toJson(je)
        } catch (e: Exception) {
            json
        }
    }

    @JvmStatic
    fun formatXml(xml: String): String {
        return try {
            val serializer = SAXTransformerFactory.newInstance().newTransformer()
            serializer.setOutputProperty(OutputKeys.INDENT, "yes")
            serializer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2")
            val xmlSource: Source = SAXSource(InputSource(ByteArrayInputStream(xml.toByteArray())))
            val res = StreamResult(ByteArrayOutputStream())
            serializer.transform(xmlSource, res)
            String((res.outputStream as ByteArrayOutputStream).toByteArray())
        } catch (e: Exception) {
            xml
        }
    }

    fun getShareText(context: Context, transaction: HttpTransaction): String {
        val text = StringBuilder("")
        text.append(
            """
    ${context.getString(R.string.interceptor_url)}: ${v(transaction.url)}
    
    """.trimIndent()
        )
        text.append(
            """
    ${context.getString(R.string.interceptor_method)}: ${v(transaction.method)}
    
    """.trimIndent()
        )
        text.append(
            """
    ${context.getString(R.string.interceptor_protocol)}: ${v(transaction.protocol)}
    
    """.trimIndent()
        )
        text.append(
            """
    ${context.getString(R.string.interceptor_status)}: ${v(transaction.status.toString())}
    
    """.trimIndent()
        )
        text.append(
            """
    ${context.getString(R.string.interceptor_response)}: ${v(transaction.responseSummaryText)}
    
    """.trimIndent()
        )
        text.append(
            """
    ${context.getString(R.string.interceptor_ssl)}: ${v(context.getString(if (transaction.isSsl) R.string.interceptor_yes else R.string.interceptor_no))}
    
    """.trimIndent()
        )
        text.append("\n")
        text.append(
            """
    ${context.getString(R.string.interceptor_request_time)}: ${v(transaction.requestDateString)}
    
    """.trimIndent()
        )
        text.append(
            """
    ${context.getString(R.string.interceptor_response_time)}: ${v(transaction.responseDateString)}
    
    """.trimIndent()
        )
        text.append(
            """
    ${context.getString(R.string.interceptor_duration)}: ${v(transaction.durationString)}
    
    """.trimIndent()
        )
        text.append("\n")
        text.append(
            """
    ${context.getString(R.string.interceptor_request_size)}: ${v(transaction.requestSizeString)}
    
    """.trimIndent()
        )
        text.append(
            """
    ${context.getString(R.string.interceptor_response_size)}: ${v(transaction.responseSizeString)}
    
    """.trimIndent()
        )
        text.append(
            """
    ${context.getString(R.string.interceptor_total_size)}: ${v(transaction.totalSizeString)}
    
    """.trimIndent()
        )
        text.append("\n")
        text.append(
            """---------- ${context.getString(R.string.interceptor_request)} ----------

"""
        )
        var headers = formatHeaders(transaction.getRequestHeaders(), false)
        if (!TextUtils.isEmpty(headers)) {
            text.append(
                """
    $headers
    
    """.trimIndent()
            )
        }
        text.append(
            if (transaction.requestBodyIsPlainText()) v(transaction.formattedRequestBody) else context.getString(
                R.string.interceptor_body_omitted
            )
        )
        text.append("\n\n")
        text.append(
            """---------- ${context.getString(R.string.interceptor_response)} ----------

"""
        )
        headers = formatHeaders(transaction.getResponseHeaders(), false)
        if (!TextUtils.isEmpty(headers)) {
            text.append(
                """
    $headers
    
    """.trimIndent()
            )
        }
        text.append(
            if (transaction.responseBodyIsPlainText()) v(transaction.formattedResponseBody) else context.getString(
                R.string.interceptor_body_omitted
            )
        )
        return text.toString()
    }

    fun getShareCurlCommand(transaction: HttpTransaction): String {
        var compressed = false
        var curlCmd = "curl"
        curlCmd += " -X " + transaction.method
        val headers = transaction.getRequestHeaders()
        var i = 0
        val count = headers.size
        while (i < count) {
            val name = headers[i].name
            val value = headers[i].value
            if ("Accept-Encoding".equals(name, ignoreCase = true) && "gzip".equals(
                    value,
                    ignoreCase = true
                )
            ) {
                compressed = true
            }
            curlCmd += " -H \"$name: $value\""
            i++
        }
        val requestBody = transaction.requestBody
        if (requestBody != null && requestBody.length > 0) {
            // try to keep to a single line and use a subshell to preserve any line breaks
            curlCmd += " --data $'" + requestBody.replace("\n", "\\n") + "'"
        }
        curlCmd += (if (compressed) " --compressed " else " ") + transaction.url
        return curlCmd
    }

    private fun v(string: String?): String {
        return string ?: ""
    }
}