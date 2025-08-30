<html>
<#-- @ftlvariable name="data" type="io.qameta.allure.attachment.http.HttpResponseAttachment" -->
<head>
    <meta http-equiv="content-type" content="text/html; charset=UTF-8">
    <title>HTTP Response Details</title>

    <!-- Подключение стилей и скриптов Highlight.js -->
    <link href="https://cdnjs.cloudflare.com/ajax/libs/highlight.js/11.8.0/styles/github-dark.min.css"  rel="stylesheet">
    <script src="https://cdnjs.cloudflare.com/ajax/libs/highlight.js/11.8.0/highlight.min.js"></script>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/highlight.js/11.8.0/languages/json.min.js"></script>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/highlight.js/11.8.0/languages/xml.min.js"></script>
    <script>hljs.highlightAll();</script>

    <style>
        body {
            font-family: Arial, sans-serif;
            background-color: #f9f9f9;
            padding: 20px;
        }
        h4 {
            color: #333;
            margin-top: 20px;
        }
        pre {
            white-space: pre-wrap;
            word-wrap: break-word;
            background-color: #2d2d2d;
            color: #ccc;
            padding: 10px;
            border-radius: 5px;
        }
        .section {
            margin-bottom: 20px;
        }
    </style>
</head>
<body>
    <div class="section">
        <h4>Status Code</h4>
        <pre><code><b>${data.responseCode}</b></code></pre>
    </div>

    <div class="section">
        <h4>Response URL</h4>
        <pre><code>
        <#if data?? && data.url??>
            ${data.url}
        <#else>
            No URL available
        </#if>
            </code></pre>
    </div>

    <#if (data.headers)?has_content>
        <div class="section">
            <h4>Headers</h4>
            <#list data.headers as name, value>
                <div>
                    <pre><code><b>${name}:</b> ${value}</code></pre>
                </div>
            </#list>
        </div>
    </#if>

    <#if data.body??>
        <div class="section">
            <h4>Body</h4>
            <pre><code class="json">${data.body}</code></pre>
        </div>
    </#if>

    <#if (data.cookies)?has_content>
        <div class="section">
            <h4>Cookies</h4>
            <#list data.cookies as name, value>
                <div>
                    <pre><code><b>${name}:</b> ${value}</code></pre>
                </div>
            </#list>
        </div>
    </#if>
</body>
</html>