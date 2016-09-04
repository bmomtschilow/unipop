package org.unipop.process.local;

import com.google.common.collect.Sets;
import org.apache.tinkerpop.gremlin.process.traversal.Traversal;
import org.apache.tinkerpop.gremlin.process.traversal.TraversalStrategy;
import org.apache.tinkerpop.gremlin.process.traversal.step.branch.LocalStep;
import org.apache.tinkerpop.gremlin.process.traversal.strategy.AbstractTraversalStrategy;
import org.apache.tinkerpop.gremlin.process.traversal.util.TraversalHelper;
import org.unipop.process.edge.EdgeStepsStrategy;
import org.unipop.process.properties.UniGraphPropertiesStrategy;
import org.unipop.process.reduce.UniGraphReduceStrategy;
import org.unipop.process.start.UniGraphStartStepStrategy;
import org.unipop.query.aggregation.LocalQuery;
import org.unipop.structure.UniGraph;

import java.util.List;
import java.util.Set;

/**
 * Created by sbarzilay on 9/4/16.
 */
public class UniGraphLocalStrategy extends AbstractTraversalStrategy<TraversalStrategy.ProviderOptimizationStrategy> implements TraversalStrategy.ProviderOptimizationStrategy {
    @Override
    public Set<Class<? extends ProviderOptimizationStrategy>> applyPrior() {
        return Sets.newHashSet(UniGraphStartStepStrategy.class, UniGraphPropertiesStrategy.class, UniGraphReduceStrategy.class, EdgeStepsStrategy.class);
    }

    @Override
    public void apply(Traversal.Admin<?, ?> traversal) {
        UniGraph uniGraph = (UniGraph) traversal.getGraph().get();
        TraversalHelper.getStepsOfAssignableClass(LocalStep.class, traversal).forEach(localStep -> {
            Traversal.Admin localTraversal = (Traversal.Admin) localStep.getLocalChildren().get(0);
            List<LocalQuery.LocalController> localControllers = uniGraph.getControllerManager()
                    .getControllers(LocalQuery.LocalController.class);
            UniGraphLocalStep uniGraphLocalStep = new UniGraphLocalStep(traversal, localTraversal, localControllers);
            TraversalHelper.replaceStep(localStep, uniGraphLocalStep, traversal);
        });
    }
}