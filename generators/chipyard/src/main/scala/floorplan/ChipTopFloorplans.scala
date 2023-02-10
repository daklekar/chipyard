// See LICENSE for license details
package chipyard.floorplan

import chipyard.TestHarness
import chipyard.{ChipTopLazyRawModuleImp, BuildSystem, DigitalTop}
import freechips.rocketchip.config.{Parameters}
import barstools.floorplan.chisel.{FloorplanAspect, Floorplan, FloorplanFunction}

import scala.collection.mutable.ListBuffer//
import freechips.rocketchip.diplomacy.LazyModule//



object ChipTopFloorplans {

  def default: FloorplanFunction = {
    case top: ChipTopLazyRawModuleImp =>
      val context = Floorplan(top, 1500.0, 1000.0)
      val topGrid = context.setTopGroup(context.createElasticArray(2))
      val tiles = top.outer.lazySystem match {
        case t: DigitalTop => t.tiles.map(x => context.addHier(x.module))
        case _ => throw new Exception("Unsupported BuildSystem type")
      }
      topGrid.placeAt(1, context.createElasticArray(tiles))
      topGrid.placeAt(0, context.createSpacer(Some("spacer")))
      context.commit()
  }

}

case object ChipTopFloorplanAspect extends FloorplanAspect[chipyard.TestHarness](
  ChipTopFloorplans.default orElse
  RocketFloorplans.default
)
