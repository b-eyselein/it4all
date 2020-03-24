package model

import model.tools.{ToolList, ToolState, ToolValues}
import sangria.macros.derive._
import sangria.schema._

object GraphQLModel {

  private implicit val toolStateEnum: EnumType[ToolState] = EnumType(
    "ToolState",
    None,
    ToolState.values.map(v => EnumValue(v.entryName, value = v)).toList
  )

  private val x = deriveEnumType[ToolState]()

  private val ToolValuesType: ObjectType[Unit, ToolValues] = deriveObjectType[Unit, ToolValues](
    ObjectTypeDescription("A tool")
  )

  private val toolId = Argument("toolId", StringType)

  private val toolValues = ToolList.toolMains.map(_.toolValues)

  private val QueryType: ObjectType[Unit, Unit] = ObjectType(
    "Query",
    fields[Unit, Unit](
      Field("tools", ListType(ToolValuesType), resolve = _ => toolValues),
      Field(
        "tool",
        OptionType(ToolValuesType),
        arguments = toolId :: Nil,
        resolve = ctx => toolValues.find(_.id == ctx.arg(toolId))
      )
    )
  )

  val schema: Schema[Unit, Unit] = Schema(QueryType)

}
