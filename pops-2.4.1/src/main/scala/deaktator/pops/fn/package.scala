package deaktator.pops

/**
  * This package provides definitions necessary create accessor functions that can extract
  * data from Protocol Buffer instances using PB's reflection APIs.
  *
  - [[deaktator.pops.fn.Caster]] provides the means to appropriately cast untyped values return
    by PB's reflection APIs to typed values.
  - [[deaktator.pops.fn.ProtoAccessor]] provides the ''trait'' and factory methods (''in the companion
    object'') to either '''explicit''' ''or'' '''implicitly''' create [[deaktator.pops.fn.ProtoAccessor]]
    instances from string-based paths.
  * @author deaktator
  */
package object fn