/*
 * This file is part of ViaMCP - https://github.com/FlorianMichael/ViaMCP
 * Copyright (C) 2020-2023 FlorianMichael/EnZaXD and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package dev.onlooker.utils.addons.viamcp.viamcp;

import dev.onlooker.utils.addons.viamcp.vialoadingbase.ViaLoadingBase;
import dev.onlooker.utils.addons.viamcp.viamcp.gui.AsyncVersionSlider;
import dev.onlooker.utils.addons.viamcp.viamcp.protocolinfo.ProtocolInfo;
import lombok.Getter;

import java.io.File;
import java.util.concurrent.CompletableFuture;

@Getter
public class ViaMCP {
    public final static int NATIVE_VERSION = 47;
    public static ViaMCP INSTANCE;

    public static void create() {
        INSTANCE = new ViaMCP();
    }

    private AsyncVersionSlider asyncVersionSlider;

    public ViaMCP() {
        ViaLoadingBase.ViaLoadingBaseBuilder.create()
                .runDirectory(new File("ViaMCP"))
                .nativeVersion(ProtocolInfo.R1_12_2.getProtocolVersion().getVersion())
                .onProtocolReload(comparableProtocolVersion -> {
                    if (getAsyncVersionSlider() != null) {
                        getAsyncVersionSlider().setVersion(comparableProtocolVersion.getVersion());
                    }
                })
                .build();
    }

    public void initAsyncSlider() {
        this.initAsyncSlider(5, 5, 110, 20);
    }

    public void initAsyncSlider(int x, int y, int width, int height) {
        CompletableFuture.runAsync(() -> asyncVersionSlider = new AsyncVersionSlider(-1, x, y, Math.max(width, 110), height));
    }
}