import java.io.{OutputStream, FileOutputStream, File}

import org.apache.jena.riot.{Lang, RDFDataMgr}

/**
  * Created by dcollarana on 8/16/2016.
  */
object Main {

  def main(args: Array[String]) = {

    val x = "SaveInFile"
    val inferenceMethod = InferenceMethod.SPARQL

    val files = List(
                      getClass.getClassLoader.getResource("GO_2014_unified_for_materialization.nt").getPath //dataset molecules
                      ,getClass.getClassLoader.getResource("GO_2014_MaterializedAnnotations.nt").getPath //vocabulary e.g. DBpedia Ontology
                      ,getClass.getClassLoader.getResource("go_2014.owl").getPath //vocabulary e.g. DBpedia Ontology
                      //,getClass.getClassLoader.getResource("additionalSem.nt").getPath //additional semantics
                      //,getClass.getClassLoader.getResource("categoryHierarchy.ttl").getPath //specific DBpedia category herachy
                    )

    println("Starting the reasoner!")

    //for merging use mergeModels method, for infering use inferModels method
    val infeModel = RdfsReasoner.inferModel(
                                        files.map(RDFDataMgr.loadModel(_))
                                        , inferenceMethod)

    println("Inferred Model Size: "+infeModel.size())

    x match {
      case "Print" => {
        println("Inferred Model Size: "+infeModel.size())
        val listStatements = infeModel.listStatements()
        while (listStatements.hasNext)
          println(listStatements.nextStatement().toString)
      }
      case "SaveInFile" => {
        val outPutFile = "C:\\DIC\\Temp\\materialized-knowledge0.nt"
        //Writing infered model in a File
        val file = new File(outPutFile)
        val fop = new FileOutputStream(file).asInstanceOf[OutputStream]
        // if file doesnt exists, then create it
        if (!file.exists()) {
          file.createNewFile()
        }
        RDFDataMgr.write(fop, infeModel, Lang.NTRIPLES)
      }
    }
  }
}

//PREFIX fs: <http://vocab.lidakra.de/fuhsen#>
//
//select ?s ?sc ?sp where
//  {
//    ?s ?p ?o .
//    ?s a ?type .
//    OPTIONAL{ ?type rdfs:subClassOf* ?sc . }
//    OPTIONAL{ ?p  rdfs:subPropertyOf* ?sp . }
//  }

/******************************
  *  Example source code
  * *****************************/
//println(inf.getDeductionsModel)

//val listStatements =inf.listStatements()
//while (listStatements.hasNext)
//  println(listStatements.nextStatement().toString)

//println("Size: "+inf.getDeductionsModel.size())

/*val i = inf.li.listStatements(person, null, (RDFNode) null);
while(i.hasNext()){
  Statement s = i.nextStatement();
  System.out.println("Statement: \n" + s.asTriple()); //turtle format
}*/

/*val f = new File(getClass.getClassLoader.getResource("movies.txt").getPath)
Source
  .fromFile(f)
  .getLines
  .foreach { line =>
    println(line)
}*/

// Reading fuhsen data
//val model = RDFDataMgr.loadModel(getClass.getClassLoader.getResource("fuhsen.ttl").getPath)