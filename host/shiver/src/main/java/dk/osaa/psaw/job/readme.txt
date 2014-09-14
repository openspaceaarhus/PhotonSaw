This package contains the files relating to the document model of the host software,
a loaded Job has the following hierachy:

Job contains one rootNode:
  JobNodeGroup contains any number of JobNodeInterface objects, which is is either JobNodeGroup or: 
    JobNode
    
JobNodeInterface contains a render method which is used to render onto a JobRenderTarget while applying a transformation.

