package org.fundacionjala.gradle.plugins.enforce.filemonitor

import com.twmacinta.util.MD5
import groovy.util.slurpersupport.GPathResult

/**
 * This class parses the sub components from object component XML file to a Map
 */
class ObjectParser {

    ArrayList<String> customFields = ['ActionOverride', 'BusinessProcess', 'CompactLayout' , 'CustomField',
                                      'FieldSet', 'HistoryRetentionPolicy', 'ListView', 'RecordType', 'SearchLayouts',
                                      'SharingReason', 'SharingRecalculation', 'ValidationRule', 'Weblink', 'fields', 'fieldSets']

    public ObjectParser() {

    }

    /**
     * parses the custom fields from object component in a xml file to a Map as
     * key is composed by custom field type / custom field fullName
     * value is composed by custom field's content in hash code.
     * @param file that contains a object component xml.
     * @return a Map with all sub components
     */
    public Map<String, String> parseByObjectXML(File file) {
        Map<String, String> result = [:]
        XmlSlurper xmlSlurper = new XmlSlurper()
        GPathResult customObject = xmlSlurper.parseText(file.text)
        customFields.each {String customField ->
            Object fieldValue = customObject.getProperty(customField)
            if (!fieldValue.toString().isEmpty()) {
                fieldValue.each { subComponent ->
                    MD5 md5 = new MD5()
                    md5.Update(fieldValue.toString())
                    String fieldName = "${customField}/${subComponent.fullName}"
                    String signature = md5.asHex()
                    result.put(fieldName, signature)
                }
            }
        }
        return result
    }
}
