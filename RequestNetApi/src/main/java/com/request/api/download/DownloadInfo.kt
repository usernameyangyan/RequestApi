package com.request.api.download

/**
Create by yangyan
Create time:2023/9/6 14:05
Describe:
 */
data class DownloadInfo(
    var url: String,
    var total: Long,
    var progress: Long,
    var fileName: String,
    var filePath: String,
    var isFinish: Boolean
)
