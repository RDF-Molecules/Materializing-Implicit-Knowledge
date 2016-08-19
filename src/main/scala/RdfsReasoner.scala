import org.apache.jena.ontology.{OntClass, OntModelSpec}
import org.apache.jena.query.{QueryFactory, QueryExecutionFactory}
import org.apache.jena.rdf.model.{Model, ModelFactory}
import org.apache.jena.reasoner.ReasonerRegistry
import org.apache.jena.riot.RDFDataMgr
/**
  * Created by dcollarana on 8/12/2016.
  */
object RdfsReasoner {

  def inferModel(model : Model, method: String): Model = {
    if (method == InferenceMethod.JENA)
      inferModelWithJena(model)
    else
      inferModelWithSparql(model)
  }

  private def inferModelWithJena(model : Model): Model = {
    // RDFS_MEM_RDFS_INF
    val ontoModel = ModelFactory.createOntologyModel(OntModelSpec.RDFS_MEM)
    // Read the RDF/XML file
    ontoModel.add(model)
    //Configuring the reasoner
    var reasoner = ReasonerRegistry.getRDFSSimpleReasoner()
    reasoner = reasoner.bindSchema(ontoModel)
    reasoner.setDerivationLogging(true)
    //returning the inferred model
    ModelFactory.createInfModel(reasoner, model)
  }

  private def inferModelWithSparql(model : Model): Model = {
    val query = QueryFactory.create(
      s"""
         |PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
         |PREFIX foaf: <http://xmlns.com/foaf/0.1/>
         |PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
         |CONSTRUCT   {
         |  ?s a ?sc .
         |  ?s ?sp ?o .
         |}
         |WHERE
         |{
         |  ?s ?p ?o .
         |  ?s a foaf:Person .
         |  OPTIONAL{ foaf:Person rdfs:subClassOf* ?sc . }
         |  OPTIONAL{ ?p  rdfs:subPropertyOf* ?sp . }
         |}
          """.stripMargin)
    QueryExecutionFactory.create(query, model).execConstruct()
  }

  def inferModel(filePath: String, method: String) : Model = {
    // Reading fuhsen data
    inferModel(RDFDataMgr.loadModel(filePath), method)
  }

  def inferModel(sourceFilePath: String, method: String, destinyFilePath: String) = {
    println("Hello World")
  }

}

//ToDo replace with Enumeration
object InferenceMethod {
  val JENA = "JENA"
  val SPARQL = "SPARQL"
}

/*
If it's a text file, and you want to limit yourself to Scala and Java, then using scala.io.Source to do the reading is probably the fastest--it's not built in, but easy to write:

def inputToFile(is: java.io.InputStream, f: java.io.File) {
  val in = scala.io.Source.fromInputStream(is)
  val out = new java.io.PrintWriter(f)
  try { in.getLines().foreach(out.print(_)) }
  finally { out.close }
}
 */