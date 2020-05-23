package io.jenkins.update_center.json;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONField;
import io.jenkins.update_center.MavenRepository;
import io.jenkins.update_center.PluginUpdateCenterEntry;
import io.jenkins.update_center.Plugin;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class UpdateCenterRoot extends WithSignature {
    @JSONField
    public final String updateCenterVersion = "1";

    @JSONField
    public String connectionCheckUrl = "http://www.google.com/"; // TODO pass in command line arg

    @JSONField
    public String id = "default"; // TODO pass in command line arg

    @JSONField
    public UpdateCenterCore core;

    @JSONField
    public Map<String, PluginUpdateCenterEntry> plugins = new TreeMap<>();

    @JSONField
    public List<UpdateCenterWarning> warnings;

    public UpdateCenterRoot(MavenRepository repo, File warningsJsonFile) throws IOException {
        // load warnings
        final String warningsJsonText = String.join("", Files.readAllLines(warningsJsonFile.toPath(), StandardCharsets.UTF_8));
        warnings = Arrays.asList(JSON.parseObject(warningsJsonText, UpdateCenterWarning[].class));

        for (Plugin plugin : repo.listJenkinsPlugins()) {
            PluginUpdateCenterEntry entry = new PluginUpdateCenterEntry(plugin);
            plugins.put(plugin.getArtifactId(), entry);
        }

        core = new UpdateCenterCore(repo.getJenkinsWarsByVersionNumber());
    }
}
