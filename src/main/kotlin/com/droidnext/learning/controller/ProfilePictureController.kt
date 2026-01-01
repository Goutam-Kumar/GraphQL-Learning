package com.droidnext.learning.controller

import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption
import java.util.UUID

@RestController
@RequestMapping("/api/files")
class ProfilePictureController {

    private val uploadDir = Path.of("uploads/profile-pictures")

    @PostMapping("/profile-picture")
    fun uploadProfilePicture(
        @RequestParam("file") file: MultipartFile
    ): UploadResponse {
        // Ensure folder exists
        Files.createDirectories(uploadDir)

        //Generate unique filename
        val fileName = "${UUID.randomUUID()}-${file.originalFilename}"
        val target = uploadDir.resolve(fileName)

        //Save the file
        Files.copy(file.inputStream, target, StandardCopyOption.REPLACE_EXISTING)

        return UploadResponse(
            fileName = fileName,
            url = "/uploads/profile-pictures/$fileName"
        )
    }
}

data class UploadResponse(
    val fileName: String,
    val url: String
)

