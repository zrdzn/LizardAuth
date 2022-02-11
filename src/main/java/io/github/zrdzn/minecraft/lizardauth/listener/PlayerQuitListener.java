/*
 * Copyright (c) 2022 zrdzn
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.github.zrdzn.minecraft.lizardauth.listener;

import io.github.zrdzn.minecraft.lizardauth.session.SessionManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerQuitListener implements Listener {

    private final SessionManager sessionManager;

    public PlayerQuitListener(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        this.sessionManager.deauthorizePlayer(event.getPlayer().getUniqueId());
    }

}
