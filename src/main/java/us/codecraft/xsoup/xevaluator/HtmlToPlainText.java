package us.codecraft.xsoup.xevaluator;

import org.jsoup.internal.StringUtil;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.NodeTraversor;
import org.jsoup.select.NodeVisitor;

public class HtmlToPlainText {

    public HtmlToPlainText() {
    }

    public String getPlainText(Element element) {
        HtmlToPlainText.FormattingVisitor formatter = new HtmlToPlainText.FormattingVisitor();
        NodeTraversor.traverse(formatter, element);
        return formatter.toString();
    }

    private class FormattingVisitor implements NodeVisitor {
        private static final int maxWidth = 80;
        private int width;
        private StringBuilder accum;

        private FormattingVisitor() {
            this.width = 0;
            this.accum = new StringBuilder();
        }

        @Override
        public void head(Node node, int depth) {
            String name = node.nodeName();
            if (node instanceof TextNode) {
                this.append(((TextNode) node).text());
            } else if (name.equals("li")) {
                this.append("\n * ");
            } else if (name.equals("dt")) {
                this.append("  ");
            } else if (StringUtil.in(name, "p", "h1", "h2", "h3", "h4", "h5", "tr")) {
                this.append("\n");
            }

        }

        @Override
        public void tail(Node node, int depth) {
            String name = node.nodeName();
            if (StringUtil.in(name, "br", "dd", "dt", "p", "h1", "h2", "h3", "h4", "h5")) {
                this.append("\n");
            } else if (name.equals("a")) {
                this.append(String.format(" <%s>", node.absUrl("href")));
            }

        }

        private void append(String text) {
            if (text.startsWith("\n")) {
                this.width = 0;
            }

            if (!text.equals(" ") || this.accum.length() != 0 && !StringUtil.in(this.accum.substring(this.accum.length() - 1), new String[]{" ", "\n"})) {
                if (text.length() + this.width > 80) {
                    String[] words = text.split("\\s+");

                    for (int i = 0; i < words.length; ++i) {
                        String word = words[i];
                        boolean last = i == words.length - 1;
                        if (!last) {
                            word = word + " ";
                        }

                        if (word.length() + this.width > 80) {
                            this.accum.append("\n").append(word);
                            this.width = word.length();
                        } else {
                            this.accum.append(word);
                            this.width += word.length();
                        }
                    }
                } else {
                    this.accum.append(text);
                    this.width += text.length();
                }

            }
        }

        @Override
        public String toString() {
            return this.accum.toString();
        }
    }
}
