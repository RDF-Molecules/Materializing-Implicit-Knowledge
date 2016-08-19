/**
  * Created by dcollarana on 8/16/2016.
  */
object Main {

  def main(args: Array[String]) = {
    println("Starting the reasoner!")
    val infeModel = RdfsReasoner.inferModel(getClass.getClassLoader.getResource("fuhsen.ttl").getPath, InferenceMethod.JENA)
    println("Inferred Model Size: "+infeModel.size())

    val listStatements = infeModel.listStatements()
    while (listStatements.hasNext)
      println(listStatements.nextStatement().toString)
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