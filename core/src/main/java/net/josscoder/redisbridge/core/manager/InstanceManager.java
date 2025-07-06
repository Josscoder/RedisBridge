package net.josscoder.redisbridge.core.manager;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import lombok.Getter;
import net.josscoder.redisbridge.core.data.InstanceInfo;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class InstanceManager {

    public static final Cache<String, InstanceInfo> INSTANCE_CACHE = CacheBuilder.newBuilder()
            .expireAfterWrite(10, TimeUnit.SECONDS)
            .build();

    @Getter
    private static final InstanceManager instance = new InstanceManager();

    public Map<String, InstanceInfo> map() {
        return INSTANCE_CACHE.asMap();
    }

    public int getTotalPlayerCount() {
        return map().values().stream()
                .mapToInt(InstanceInfo::getPlayers)
                .sum();
    }

    public int getTotalMaxPlayers() {
        return map().values().stream()
                .mapToInt(InstanceInfo::getMaxPlayers)
                .sum();
    }

    public InstanceInfo getInstanceById(String id) {
        return INSTANCE_CACHE.getIfPresent(id);
    }

    public Set<InstanceInfo> getGroupInstances(String group) {
        return map().values().stream()
                .filter(instance -> instance.getGroup().equalsIgnoreCase(group))
                .collect(Collectors.toSet());
    }

    public int getGroupPlayerCount(String group) {
        return getGroupInstances(group).stream()
                .mapToInt(InstanceInfo::getPlayers)
                .sum();
    }

    public int getGroupMaxPlayers(String group) {
        return getGroupInstances(group).stream()
                .mapToInt(InstanceInfo::getMaxPlayers)
                .sum();
    }

    public enum SelectionStrategy {
        RANDOM,
        LOWEST_PLAYERS,
        MOST_PLAYERS_AVAILABLE
    }

    private Optional<InstanceInfo> findInstanceByGroupAndStrategy(String group, SelectionStrategy strategy) {
        List<InstanceInfo> instances = getGroupInstances(group).stream()
                .filter(instance -> !instance.isFull())
                .distinct()
                .collect(Collectors.toList());

        switch (strategy) {
            case RANDOM:
                return Optional.of(instances.get(new Random().nextInt(instances.size())));
            case LOWEST_PLAYERS:
                return instances.stream()
                        .min(Comparator.comparingInt(InstanceInfo::getPlayers));
            case MOST_PLAYERS_AVAILABLE:
                return instances.stream()
                        .max(Comparator.comparingInt(InstanceInfo::getPlayers));
            default:
                return Optional.empty();
        }
    }

    public InstanceInfo selectAvailableInstance(String group, SelectionStrategy strategy) {
        return findInstanceByGroupAndStrategy(group, strategy).orElse(null);
    }

    public List<InstanceInfo> selectAvailableInstances(String group, SelectionStrategy strategy) {
        return findInstanceByGroupAndStrategy(group, strategy)
                .map(Collections::singletonList)
                .orElse(Collections.emptyList());
    }
}
