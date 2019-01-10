import tester.Tester;

/******************************************************************/
//           IXML DOC REPRESENTED AS LISTS OF IXMLFRAGS

/******************************************************************/
//an IXML Document is a list of XMLFrags
interface ILoXMLFrag {

  //computes the number of charecters for the content in an XML
  int contentLength();

  //Determines if the XML has a tag with the given name
  boolean hasTag(String name);

  //Determines if the XML has an attribute with the given name
  boolean hasAttribute(String name);

  //Determines if the XML has an attribute with the given name in the given tag
  boolean hasAttributeInTag(String tag, String att);

  //updates all values of the given name

  ILoXMLFrag updateAttribute(String name, String value);

  //Converts the XML to a string without the tags and attributes
  String renderAsString();
}

//A List of IXMLFrags is an IXML Document
class ConsLoXMLFrag implements ILoXMLFrag {
  IXMLFrag first;
  ILoXMLFrag rest;

  ConsLoXMLFrag(IXMLFrag first, ILoXMLFrag rest) {
    this.first = first;
    this.rest = rest;
  }

  //content length of the fragment
  public int contentLength() {
    return this.first.fragContentLength()
            + this.rest.contentLength();
  }

  //checks if the frag matches the tag
  public boolean hasTag(String name) {
    return this.first.fragHasTag(name)
            || this.rest.hasTag(name);
  }

  //checks if the frag matches the attribute
  public boolean hasAttribute(String name) {
    return this.first.fragHasAttribute(name)
            || this.rest.hasAttribute(name);
  }

  //checks if the atttribute exists in tag
  public boolean hasAttributeInTag(String tag, String att) {
    return this.first.fragHasAttributeInTag(tag, att)
            || this.rest.hasAttributeInTag(tag, att);
  }

  //updates the gigven attribute to with the given value
  public ILoXMLFrag updateAttribute(String name, String value) {
    return new ConsLoXMLFrag(this.first.fragUpdateAttribute(name, value),
            this.rest.updateAttribute(name, value));

  }

  //returns the content of the XML without the tags and attributes
  public String renderAsString() {
    return this.first.fragRenderAsString() +
            this.rest.renderAsString();
  }

}

//Empty List of XML Fragments
class MtLoXMLFrag implements ILoXMLFrag {
  //Empty LoFrag has no length
  public int contentLength() {
    return 0;
  }

  //base case
  public boolean hasTag(String name) {
    return false;
  }

  //base case
  public boolean hasAttribute(String name) {
    return false;
  }

  //base case
  public boolean hasAttributeInTag(String tag, String att) {
    return false;
  }

  //base case
  public ILoXMLFrag updateAttribute(String name, String value) {
    return new MtLoXMLFrag();
  }

  //base case
  public String renderAsString() {
    return "";
  }

}
/******************************************************************/
//         IXMLFrags represented by Plaintext and Tagged

/******************************************************************/

//Interface to represent XML Fragments
interface IXMLFrag {

  //returns the length of the fragment
  int fragContentLength();

  //checks if the frag has the given tag
  boolean fragHasTag(String name);

  //checks if the frag has the given attribute
  boolean fragHasAttribute(String name);

  //checks if the frag has the attribute in tag
  boolean fragHasAttributeInTag(String tag, String att);

  //updates the value of the frag attribute
  IXMLFrag fragUpdateAttribute(String name, String value);

  //returns the given frag as text without tags and attributes
  String fragRenderAsString();
}

//class representing plain text in an IXMLFrag
class Plaintext implements IXMLFrag {
  String txt;

  Plaintext(String txt) {
    this.txt = txt;
  }

  //returns the length of the plaintext
  public int fragContentLength() {
    return this.txt.length();
  }

  //a plaintext does not have the tag. base case
  public boolean fragHasTag(String name) {
    return false;
  }

  //a plaintext does not have the attribute. base case
  public boolean fragHasAttribute(String name) {
    return false;
  }

  //a plaintext does not have the attribute in tag. base case
  public boolean fragHasAttributeInTag(String tag, String att) {
    return false;
  }

  //no attribute to update. base case
  public IXMLFrag fragUpdateAttribute(String name, String value) {
    return new Plaintext(this.txt);
  }

  //just returns the plaintext
  public String fragRenderAsString() {
    return this.txt;
  }
}

//class representing layers of Tags
class Tagged implements IXMLFrag {
  /*
   * fields:
   * tag... Tag
   * content ... ILoXMLFrag
   *
   * methods:
   * fragHasTag(String name) ... boolean
   * fragHasAttribute(String name) ...  boolean
   * fragHasAttributeInTag(String tag, String att) ... boolean
   * fragUpdateAttribute(String name, String value) ... void
   * fragRenderAsString() ... String
   *
   * methods for fields:
   * fragContentLength() ... int
   * tagSameName(String name) ... boolean
   * tagContainsAtt(String name) ... boolean
   * tagHasAttribute(String tag, String att) ... boolean
   * tagUpdateAttribute(String name, String value) ... void
   * tagRenderAsString() ... String
   *
   */
  Tag tag;
  ILoXMLFrag content;

  Tagged(Tag tag, ILoXMLFrag content) {
    this.tag = tag;
    this.content = content;
  }

  //
  public int fragContentLength() {
    return this.content.contentLength();
  }

  //fix so they go through loop
  public boolean fragHasTag(String name) {
    return tag.tagSameName(name)
            || this.content.hasTag(name);
  }

  public boolean fragHasAttribute(String name) {
    return this.tag.tagContainsAtt(name)
            || this.content.hasAttribute(name);
  }

  public boolean fragHasAttributeInTag(String tag, String att) {
    return this.tag.tagHasAttributeInTag(tag, att)
            || this.content.hasAttributeInTag(tag, att);
  }


  public IXMLFrag fragUpdateAttribute(String name, String value) {
    return new Tagged(this.tag.tagUpdateAttribute(name, value),
            this.content.updateAttribute(name, value));

  }

  public String fragRenderAsString() {
    return "" + this.content.renderAsString();
  }

}
/******************************************************************/
//          Tag represented by a String and ILoAtt

/******************************************************************/

class Tag {
  String name;
  ILoAtt atts;

  Tag(String name, ILoAtt atts) {
    this.name = name;
    this.atts = atts;
  }

  boolean tagSameName(String givenName) {
    return this.name.equals(givenName);
  }

  public boolean tagContainsAtt(String att) {
    return this.atts.loattContainsSameAtt(att);
  }

  boolean tagHasAttributeInTag(String tag, String att) {
    return tagSameName(tag) && tagContainsAtt(att);
  }

  Tag tagUpdateAttribute(String name, String value) {
    return new Tag(this.name, this.atts.loattUpdateAttribute(name, value));
  }
}


/******************************************************************/
//      List of Attributes represented by MTLists and Lists

/******************************************************************/
//ILoAtt is either an empty list of Atts(MTLoAtt)
//or a list of Atts (LoAtt)
interface ILoAtt {
  boolean loattContainsSameAtt(String attName);

  ILoAtt loattUpdateAttribute(String name, String value);
}

//class representing Empty List of Attributes
class MtLoAtt implements ILoAtt {
  public boolean loattContainsSameAtt(String attName) {
    return false;
  }

  public ILoAtt loattUpdateAttribute(String name, String value) {
    return new MtLoAtt();
  }

}

//class representing List of Attributes
class ConsLoAtt implements ILoAtt {
  Att first;
  ILoAtt rest;

  ConsLoAtt(Att first, ILoAtt rest) {
    this.first = first;
    this.rest = rest;
  }

  public boolean loattContainsSameAtt(String attName) {
    if (this.first.sameName(attName)) {
      return true;
    }
    else {
      return this.rest.loattContainsSameAtt(attName);
    }
  }


  public ILoAtt loattUpdateAttribute(String name, String value) {
    return new ConsLoAtt(this.first.updateVal(name, value),
            this.rest.loattUpdateAttribute(name, value));
  }

}
/******************************************************************/
//  Attributes represented by a String name and String value

/******************************************************************/

//Attribute class
class Att {
  String name;
  String value;

  Att(String n, String v) {
    this.name = n;
    this.value = v;
  }

  boolean sameName(String givenName) {
    return this.name.equals(givenName);
  }

  Att updateVal(String name, String value) {
    return new Att(name, value);
  }
}


//class representing a tag around plaintext in XML


/******************************************************************/
//                  Examples         Class              haha nice

/******************************************************************/


//Examples class
class ExamplesXML {
  ILoXMLFrag xmlMt = new MtLoXMLFrag();

  IXMLFrag plaintextX = new Plaintext("X");
  IXMLFrag plaintextML = new Plaintext("ML");
  IXMLFrag plaintextIAM = new Plaintext("I am ");
  IXMLFrag plaintextXML = new Plaintext("XML");
  IXMLFrag plaintextExclam = new Plaintext("!");


  Att att1 = new Att("volume", "30db");
  Att att2 = new Att("duration", "5sec");

  ILoAtt loatts1 = new MtLoAtt();
  ILoAtt loatts2 = new ConsLoAtt(this.att1, this.loatts1);
  ILoAtt loatts3 = new ConsLoAtt(this.att2, this.loatts2);

  Tag tag1 = new Tag("yell", this.loatts1);
  Tag tag2 = new Tag("yell", this.loatts2);
  Tag tag3 = new Tag("yell", this.loatts3);
  Tag tag4 = new Tag("italic", this.loatts1);

  IXMLFrag taggedXtag = new Tagged(this.tag4, new ConsLoXMLFrag(this.plaintextX, this.xmlMt));

  IXMLFrag tagged1 = new Tagged(this.tag1, new ConsLoXMLFrag(this.plaintextXML, this.xmlMt));
  IXMLFrag tagged2 = new Tagged(this.tag1, new ConsLoXMLFrag(this.taggedXtag,
          new ConsLoXMLFrag(this.plaintextML, this.xmlMt)));

  IXMLFrag tagged3 = new Tagged(this.tag2, new ConsLoXMLFrag(this.taggedXtag,
          new ConsLoXMLFrag(this.plaintextML, this.xmlMt)));

  IXMLFrag tagged4 = new Tagged(this.tag3, new ConsLoXMLFrag(this.taggedXtag,
          new ConsLoXMLFrag(this.plaintextML, this.xmlMt)));


  ILoXMLFrag xml1 = new ConsLoXMLFrag(new Plaintext("I am XML!"), this.xmlMt);
  ILoXMLFrag xmlExclam = new ConsLoXMLFrag(this.plaintextExclam, this.xmlMt);
  ILoXMLFrag xmlXML = new ConsLoXMLFrag(this.tagged1, this.xmlExclam);


  ILoXMLFrag xmlXMLtag1 = new ConsLoXMLFrag(this.tagged2, this.xmlExclam);
  ILoXMLFrag xmlXMLtag2 = new ConsLoXMLFrag(this.tagged3, this.xmlExclam);
  ILoXMLFrag xmlXMLtag3 = new ConsLoXMLFrag(this.tagged4, this.xmlExclam);

  ILoXMLFrag xml2 = new ConsLoXMLFrag(this.plaintextIAM, this.xmlXML);

  ILoXMLFrag xml3 = new ConsLoXMLFrag(this.plaintextIAM, this.xmlXMLtag1);

  ILoXMLFrag xml4 = new ConsLoXMLFrag(this.plaintextIAM, this.xmlXMLtag2);

  ILoXMLFrag xml5 = new ConsLoXMLFrag(this.plaintextIAM, this.xmlXMLtag3);

  boolean testHasAttribute(Tester t) {
    return t.checkExpect(this.xml1.hasAttribute("volume"), false) &&
            t.checkExpect(this.xml2.hasAttribute("volume"), false) &&
            t.checkExpect(this.xml4.hasAttribute("volume"), true) &&
            t.checkExpect(this.xml5.hasAttribute("volume"), true);
  }

  boolean testHasTag(Tester t) {
    return t.checkExpect(this.xml2.hasTag("yell"), true) &&
            t.checkExpect(this.xml2.hasTag("shout"), false) &&
            t.checkExpect(this.xml3.hasTag("yell"), true) &&
            t.checkExpect(this.xml3.hasTag("shout"), false);
  }

  boolean testContentLength(Tester t) {
    return t.checkExpect(this.xml1.contentLength(), 9) &&
            t.checkExpect(this.xml1.contentLength(), 9);
  }

  boolean testHasAttributeInTag(Tester t) {
    return t.checkExpect(this.xml5.hasAttributeInTag("volume", "yell"), false) &&
            t.checkExpect(this.xml4.hasAttributeInTag("volume", "italic"), false);
  }

  boolean testUpdateAttribute(Tester t) {
    return t.checkExpect(this.xml1.updateAttribute("xml1", "hi"), this.xml1) &&
            t.checkExpect(this.xml2.updateAttribute("xml2", "bye"), this.xml2) &&
            t.checkExpect(new ConsLoXMLFrag(this.tagged3, this.xmlMt)
                            .updateAttribute("color", "blue"),
                    new ConsLoXMLFrag(
                            new Tagged(new Tag("yell", new ConsLoAtt(new Att("color", "blue"),
                                    new MtLoAtt())), new ConsLoXMLFrag(
                                    new Plaintext("Hello World!"), this.xmlMt)), this.xmlMt));
  }
}