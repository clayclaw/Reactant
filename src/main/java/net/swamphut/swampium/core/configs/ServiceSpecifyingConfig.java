package net.swamphut.swampium.core.configs;

import java.util.ArrayList;
import java.util.List;

public class ServiceSpecifyingConfig {
    public List<ServiceSpecifyingRule> specifyRules = new ArrayList<>();
    public List<ServiceSpecifyingRule> blacklistRules = new ArrayList<>();

    public class ServiceSpecifyingRule {
        public String requester = "";
        public List<ServiceProviderSpecifier> fullfillWith = new ArrayList<>();
    }

    public class ServiceProviderSpecifier {
        public String require = "";
        public String provider = "";
    }
}
