package ua.yelisieiev.web;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import ua.yelisieiev.entity.Product;

import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PageWriter {
    private final Configuration configuration;

    public PageWriter() {
        configuration = new Configuration(Configuration.VERSION_2_3_31);
        configuration.setClassForTemplateLoading(this.getClass(), "/templates");
    }

    public void writePage(String path, Map<String, Object> pageData, Writer writer) throws IOException, TemplateException {
        Template template = configuration.getTemplate(path);
        template.process(pageData, writer);
    }
}
