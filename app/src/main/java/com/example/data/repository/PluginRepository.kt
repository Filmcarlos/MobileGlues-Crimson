package com.example.data.repository

import com.example.R
import com.example.core.crash.LocalCrashHandler
import com.example.core.util.AppResult
import com.example.data.gateway.RendererGateway
import com.example.domain.model.OpenSourceLicenseItem
import com.example.domain.model.PluginOverviewSnapshot

/**
 * Coordinates [RendererGateway] and [LocalCrashHandler] to build a unified snapshot
 * of plugin readiness, launcher presence, manifest contract, and open-source notices.
 */
class PluginRepository(
    private val rendererGateway: RendererGateway,
    private val crashHandler: LocalCrashHandler
) {

    suspend fun loadPluginOverview(): AppResult<PluginOverviewSnapshot> {
        val nativeResult = rendererGateway.inspectNativeLibrary()
        if (nativeResult is AppResult.Error) return nativeResult

        val launchersResult = rendererGateway.detectInstalledLaunchers()
        if (launchersResult is AppResult.Error) return launchersResult

        val manifestResult = rendererGateway.readManifestContract()
        if (manifestResult is AppResult.Error) return manifestResult

        val nativeStatus = (nativeResult as AppResult.Success).data
        val launchers = (launchersResult as AppResult.Success).data
        val manifest = (manifestResult as AppResult.Success).data

        return AppResult.Success(
            PluginOverviewSnapshot(
                nativeStatus = nativeStatus,
                launchers = launchers,
                manifestContract = manifest,
                crashReportCount = crashHandler.getCrashReportCount(),
                latestCrashSummary = crashHandler.getLatestCrashSummary()
            )
        )
    }

    suspend fun clearCrashReports() {
        crashHandler.clearCrashReports()
    }

    fun getOpenSourceLicenses(): List<OpenSourceLicenseItem> = listOf(
        OpenSourceLicenseItem(
            id = "mobileglues",
            nameResId = R.string.about_license_mg_name,
            spdxResId = R.string.about_license_mg_spdx,
            summaryResId = R.string.about_license_mg_summary,
            fullNoticeText = LGPL_2_1_NOTICE
        ),
        OpenSourceLicenseItem(
            id = "jetpack_compose",
            nameResId = R.string.about_license_compose_name,
            spdxResId = R.string.about_license_compose_spdx,
            summaryResId = R.string.about_license_compose_summary,
            fullNoticeText = APACHE_2_0_NOTICE
        ),
        OpenSourceLicenseItem(
            id = "androidx_libraries",
            nameResId = R.string.about_license_androidx_name,
            spdxResId = R.string.about_license_androidx_spdx,
            summaryResId = R.string.about_license_androidx_summary,
            fullNoticeText = APACHE_2_0_NOTICE
        ),
        OpenSourceLicenseItem(
            id = "typography_fonts",
            nameResId = R.string.about_license_fonts_name,
            spdxResId = R.string.about_license_fonts_spdx,
            summaryResId = R.string.about_license_fonts_summary,
            fullNoticeText = OFL_1_1_NOTICE
        )
    )

    companion object {
        private const val LGPL_2_1_NOTICE = """
MobileGlues — OpenGL to OpenGL ES 3.x Translation Layer
Copyright (C) MobileGlues Contributors

This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; version 2.1 of the License.

This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.

Dynamic Linking & Relinking Notice:
This application loads `libmobileglues.so` dynamically as an Android native plugin for FCL and Zalith Launcher. Users are free to inspect, modify, and replace `lib/<abi>/libmobileglues.so` inside the APK package in accordance with LGPL-2.1 Section 6.
"""

        private const val APACHE_2_0_NOTICE = """
Copyright (C) The Android Open Source Project & JetBrains s.r.o.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
"""

        private const val OFL_1_1_NOTICE = """
Prompt Font Family — Copyright (c) Cadson Demak
JetBrains Mono Font Family — Copyright (c) JetBrains s.r.o.

This Font Software is licensed under the SIL Open Font License, Version 1.1.
Permission is hereby granted, free of charge, to any person obtaining a copy of the Font Software, to use, study, copy, merge, embed, modify, redistribute, and sell modified and unmodified copies of the Font Software.
"""
    }
}
