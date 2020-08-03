package dev.reactant.reactant.core.commands.completion

import org.bukkit.Bukkit

/**
 * We will not guarantee the correctness of following list
 * Please only use those class as picocli command candidates
 *
 * DO NOT use in your logic!
 */
class CommonCandidates {
    class Player {
        class Online {
            class Name : Iterable<String> {
                override fun iterator(): Iterator<String> = Bukkit.getOnlinePlayers().map { it.name }.iterator()
            }

            class UUID : Iterable<String> {
                override fun iterator(): Iterator<String> = Bukkit.getOnlinePlayers().map { it.uniqueId.toString() }.iterator()
            }
        }

        class Offline {
            class Name : Iterable<String> {
                override fun iterator(): Iterator<String> = Bukkit.getOnlinePlayers().map { it.name }.iterator()
            }

            class UUID : Iterable<String> {
                override fun iterator(): Iterator<String> = Bukkit.getOnlinePlayers().map { it.uniqueId.toString() }.iterator()
            }
        }

        class Banned {
            class Name : Iterable<String> {
                override fun iterator(): Iterator<String> = Bukkit.getBannedPlayers().mapNotNull { it.name }.iterator()
            }

            class UUID : Iterable<String> {
                override fun iterator(): Iterator<String> = Bukkit.getBannedPlayers().map { it.uniqueId.toString() }.iterator()
            }

            class IP : Iterable<String> {
                override fun iterator(): Iterator<String> = Bukkit.getIPBans().iterator()
            }
        }


        class Operator {
            class Name : Iterable<String> {
                override fun iterator(): Iterator<String> = Bukkit.getOperators().mapNotNull { it.name }.iterator()
            }

            class UUID : Iterable<String> {
                override fun iterator(): Iterator<String> = Bukkit.getOperators().map { it.uniqueId.toString() }.iterator()
            }
        }

        class Whitelisted {
            class Name : Iterable<String> {
                override fun iterator(): Iterator<String> = Bukkit.getWhitelistedPlayers().mapNotNull { it.name }.iterator()
            }

            class UUID : Iterable<String> {
                override fun iterator(): Iterator<String> = Bukkit.getWhitelistedPlayers().map { it.uniqueId.toString() }.iterator()
            }
        }

    }

    class World {
        class Name : Iterable<String> {
            override fun iterator(): Iterator<String> = Bukkit.getWorlds().map { it.name }.iterator()
        }

        class UUID : Iterable<String> {
            override fun iterator(): Iterator<String> = Bukkit.getWorlds().map { it.uid.toString() }.iterator()
        }
    }
}
