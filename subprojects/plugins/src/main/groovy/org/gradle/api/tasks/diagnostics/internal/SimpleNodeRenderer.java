/*
 * Copyright 2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.gradle.api.tasks.diagnostics.internal;

import org.gradle.api.tasks.diagnostics.internal.dependencies.RenderableDependency;
import org.gradle.logging.StyledTextOutput;

import java.util.Set;

import static org.gradle.logging.StyledTextOutput.Style.Info;

/**
* by Szczepan Faber, created at: 9/21/12
*/
class SimpleNodeRenderer implements NodeRenderer {
    public void renderNode(StyledTextOutput output, RenderableDependency node, Set<RenderableDependency> children, boolean alreadyRendered) {
        output.text(node.getName());
        if (alreadyRendered) {
            output.withStyle(Info).text(" (*)");
        }
    }
}