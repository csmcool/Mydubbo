package xyz.coolcsm.mydubbo.common;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import java.io.InputStream;

/**
 * @author 什锦
 * @Package xyz.coolcsm.mydubbo.common
 * @since 2022/4/23 21:17
 */

public class XmlUtil {

    private Document document;
    private boolean validation;
    private XPath xpath;

    XmlUtil(InputStream in) {
        commonConstructor(true);
        //初始化当前类的document对象，创建Document对象
        this.document = createDocument(new InputSource(in));
    }

    public void commonConstructor(boolean validation) {
        this.validation = validation;
        XPathFactory factory = XPathFactory.newInstance();
        this.xpath = factory.newXPath();
    }

    /**
     * 评估、评价、计算 节点的值
     *
     * @param expression
     * @return
     */
    public Node evalNode(String expression) {
        //jdk的xml文件一个节点
        Node node = (Node) evaluate(expression, document, XPathConstants.NODE);
        if (node == null) {
            return null;
        }
        //XNode是mybatis封装的，代表xml节点的一个对象
        return node;
    }

    private Object evaluate(String expression, Object root, QName returnType) {
        try {
            return xpath.evaluate(expression, root, returnType);
        } catch (Exception e) {
            throw new RuntimeException("Error evaluating XPath.  Cause: " + e, e);
        }
    }

    /**
     * 创建Document对象
     *
     * @param inputSource
     * @return
     */
    private Document createDocument(InputSource inputSource) {
        // important: this must only be called AFTER common constructor
        try {
            //JDK提供的文档解析工厂对象
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            //设置是否验证
            factory.setValidating(validation);
            //设置是否支持命名空间
            factory.setNamespaceAware(false);
            //设置是否忽略注释
            factory.setIgnoringComments(true);
            //设置是否忽略元素内容的空白
            factory.setIgnoringElementContentWhitespace(false);
            factory.setCoalescing(false);
            factory.setExpandEntityReferences(true);

            //创建一个DocumentBuilder对象
            DocumentBuilder builder = factory.newDocumentBuilder();

            //设置解析文档错误的处理
            builder.setErrorHandler(new ErrorHandler() {
                @Override
                public void error(SAXParseException exception) throws SAXException {
                    throw exception;
                }

                @Override
                public void fatalError(SAXParseException exception) throws SAXException {
                    throw exception;
                }

                @Override
                public void warning(SAXParseException exception) throws SAXException {
                }
            });

            //解析输入源的xml数据为一个Document文件
            return builder.parse(inputSource);

        } catch (Exception e) {
            throw new RuntimeException("Error creating document instance.  Cause: " + e, e);
        }
    }


}
