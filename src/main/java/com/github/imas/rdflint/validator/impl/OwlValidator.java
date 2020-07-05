package com.github.imas.rdflint.validator.impl;

import com.github.imas.rdflint.LintProblem;
import com.github.imas.rdflint.LintProblemSet;
import com.github.imas.rdflint.validator.AbstractRdfValidator;
import java.util.List;
import java.util.Map;
import org.apache.jena.graph.Triple;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.reasoner.ValidityReport;
import org.apache.log4j.Logger;

public class OwlValidator extends AbstractRdfValidator {

  private static final Logger logger = Logger.getLogger(OwlValidator.class.getName());

  private OntModel model;

  @Override
  public void prepareValidationResource(Map<String, List<Triple>> fileTripleSet) {
    this.model = ModelFactory.createOntologyModel(OntModelSpec.OWL_DL_MEM_RULE_INF, this.model);
  }

  @Override
  public void validateTripleSet(LintProblemSet problems, String file, List<Triple> tripleSet) {
    tripleSet.forEach(t -> this.model.add(this.model.asStatement(t)));
  }

  @Override
  public void reportAdditionalProblem(LintProblemSet problems) {
    if (logger.isTraceEnabled()) {
      logger.trace("reportAdditionalProblem: in (this.model.size()=" + this.model.size() + ")");
    }

    ValidityReport report = model.validate();
    if (report != null && !report.isClean()) {
      report.getReports().forEachRemaining(r -> problems.addProblem("OWL_Additional_Check",
          new LintProblem(LintProblem.ErrorLevel.WARN, this, null, "owlViolation", r.description)));
    }

    if (logger.isTraceEnabled()) {
      logger.trace("reportAdditionalProblem: out (report=" + report + ")");
    }
  }

  @Override
  public void close() {
    this.model.close();
  }

}
