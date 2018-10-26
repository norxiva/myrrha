package my.norxiva.myrrha.channel;

import freemarker.cache.ClassTemplateLoader;
import freemarker.cache.MultiTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.Map;

/**
 * When you create this helper please make sure only one instance is created for individual module.
 */
@Slf4j
public class FreeMarkerHelper {
    private volatile Configuration cfg;

    public FreeMarkerHelper(String... templatePaths) {
        cfg = new Configuration(Configuration.VERSION_2_3_22);
        cfg.setDefaultEncoding("UTF-8");
        MultiTemplateLoader mtl = new MultiTemplateLoader(
                Arrays.stream(templatePaths)
                        .map(t -> new ClassTemplateLoader(getClass(), t))
                        .toArray(ClassTemplateLoader[]::new));
        cfg.setTemplateLoader(mtl);
        try {
            cfg.setSetting(Configuration.TEMPLATE_UPDATE_DELAY_KEY_SNAKE_CASE,
                    Integer.toString(Integer.MAX_VALUE));
        } catch (TemplateException ex) {
            log.error("Failed to set cache expiration time for freemarker configuration...", ex);
        }
        cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
    }

    public Configuration getConfiguration() {
        return cfg;
    }

    /**
     * Get filled content of template.
     */
    public String render(Template template, Map data) {
        try {
            StringWriter writer = new StringWriter();
            template.process(data, writer);
            return writer.toString();
        } catch (TemplateException | IOException ex) {
            String name = template.getName();
            log.error("Failed to render the template - " + name, ex);
            throw new ThirdPartyException("Error rendering the template - " + name, ex);
        }
    }
}
