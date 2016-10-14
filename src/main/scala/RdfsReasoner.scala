import org.apache.jena.ontology.{OntClass, OntModelSpec}
import org.apache.jena.query.{QueryFactory, QueryExecutionFactory}
import org.apache.jena.rdf.model.{Model, ModelFactory}
import org.apache.jena.reasoner.ReasonerRegistry
import org.apache.jena.riot.{Lang, RDFDataMgr}
import java.io._
/**
  * Created by dcollarana on 8/12/2016.
  */
object RdfsReasoner {


  def inferModel(models: List[Model], method: String): Model = {
    if (method == InferenceMethod.JENA)
      inferModelWithJena(
        mergeModels(models)
      )
    else
      inferModelWithSparql(
        mergeModels(models)
      )
  }

  private def mergeModels(models : List[Model]) : Model = {
    val model = ModelFactory.createDefaultModel()
    models.map( current => model.add(current))
    model
  }

  private def inferModelWithJena(model : Model): Model = {
    // RDFS_MEM_RDFS_INF
    val ontoModel = ModelFactory.createOntologyModel(OntModelSpec.RDFS_MEM_RDFS_INF)
    // Read the RDF/XML file
    ontoModel.add(model)
    //Configuring the reasoner
    var reasoner = ReasonerRegistry.getRDFSReasoner
    reasoner = reasoner.bindSchema(ontoModel)
    reasoner.setDerivationLogging(true)
    //returning the inferred model
    ModelFactory.createInfModel(reasoner, model).getDeductionsModel
    //filterInfModel(ModelFactory.createInfModel(reasoner, model).getDeductionsModel)
  }

  private def filterInfModel(infModel : Model) : Model = {
    val query = QueryFactory.create(
      s"""
         |PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
         |PREFIX foaf: <http://xmlns.com/foaf/0.1/>
         |PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
         |PREFIX dct: <http://purl.org/dc/terms/>
         |PREFIX skos: <http://www.w3.org/2004/02/skos/core#>
         |
         | CONSTRUCT   {
         |  ?s ?p ?o .
         |}
         |WHERE
         |{
         |  ?s a foaf:Person .
         |  ?s ?p ?o .
         |}
          """.stripMargin)
    QueryExecutionFactory.create(query, infModel).execConstruct()
  }

  private def inferModelWithSparql(model : Model): Model = {
    val query = QueryFactory.create(
      s"""
         |PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
         |PREFIX foaf: <http://xmlns.com/foaf/0.1/>
         |PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
         |PREFIX dct: <http://purl.org/dc/terms/>
         |PREFIX skos: <http://www.w3.org/2004/02/skos/core#>
         |PREFIX owl: <http://www.w3.org/2002/07/owl#>
         |
         |CONSTRUCT   {
         |  ?s <http://example.org/GO#hasAnnotation> ?sc .
         |}
         |WHERE
         |{
         |  ?s <http://example.org/GO#hasAnnotation> ?o .
         |  ?o a owl:Class .
         |  ?o rdfs:subClassOf* ?sc .
         |  Filter (!isBlank(?sc)) .
         |}
          """.stripMargin)
    QueryExecutionFactory.create(query, model).execConstruct()
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

/*
PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
PREFIX foaf: <http://xmlns.com/foaf/0.1/>
PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
PREFIX dct: <http://purl.org/dc/terms/>
PREFIX skos: <http://www.w3.org/2004/02/skos/core#>

 CONSTRUCT   {
  ?s a ?sc .
  ?s ?sp ?o .
  ?s dct:subject ?broaderSub .
}
WHERE
{
  ?s ?p ?o .
  ?s a foaf:Person .
  OPTIONAL{ foaf:Person rdfs:subClassOf* ?sc . }
  OPTIONAL{ ?p  rdfs:subPropertyOf* ?sp . }
  OPTIONAL{
            ?o  dct:subject ?spb .
            ?spb skos:broader* ?broaderSub .
	          Filter (!isBlank(?broaderSub )) .
          }
}
 */

/*
PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
PREFIX foaf: <http://xmlns.com/foaf/0.1/>
PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
PREFIX dct: <http://purl.org/dc/terms/>
PREFIX skos: <http://www.w3.org/2004/02/skos/core#>

CONSTRUCT   {
  ?s ?p ?sc .
  ?s ?sp ?o .
}
WHERE
{
  ?s ?p ?o .
  ?o rdfs:subClassOf* ?sc .
  ?p rdfs:subPropertyOf* ?sp .
}
 */

/*
PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
PREFIX foaf: <http://xmlns.com/foaf/0.1/>
PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
PREFIX dct: <http://purl.org/dc/terms/>
PREFIX skos: <http://www.w3.org/2004/02/skos/core#>
PREFIX owl: <http://www.w3.org/2002/07/owl#>

CONSTRUCT   {
  ?s <http://example.org/GO#hasAnnotation> ?sc .
}
WHERE
{
  ?s <http://example.org/GO#hasAnnotation> ?o .
  ?o a owl:Class .
  ?o rdfs:subClassOf* ?sc .
  Filter (!isBlank(?sc)) .
}
 */