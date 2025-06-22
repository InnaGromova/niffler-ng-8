<html>
<#-- @ftlvariable name="data" type="io.qameta.allure.attachment.http.HttpRequestAttachment" -->
<head>
    <meta http-equiv="content-type" content="text/html; charset=UTF-8">
    <title>HTTP Request Details</title>

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
        <h4>Request Method</h4>
        <pre><code>${data.method}</code></pre>
    </div>

    <div class="section">
        <h4>Request URL</h4>
        <pre><code>${data.url}</code></pre>
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
</body>
</html>