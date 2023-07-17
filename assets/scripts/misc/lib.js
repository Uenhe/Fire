var mod = Vars.mods.locateMod("fire");



/*========Single Research========*/
exports.addToResearch = (content, research) => {

    var researchName = research.parent;
    var customRequirements = research.requirements;
    var objectives = research.objectives;
    var lastNode = TechTree.all.find(t => t.content == content);

    if(lastNode != null) lastNode.remove();

    var node = new TechTree.TechNode(null, content, customRequirements !== undefined ? customRequirements : content.researchRequirements());

    if(objectives) node.objectives.addAll(objectives);
    if(node.parent != null) node.parent.children.remove(node);

    var parent = TechTree.all.find(t => t.content.name.equals(researchName) || t.content.name.equals(mod.name + "-" + researchName));

    if(!parent.children.contains(node)) parent.children.add(node);

    node.parent = parent;

}
