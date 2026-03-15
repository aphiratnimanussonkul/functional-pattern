// import cats.effect.IO
// import cats.effect.unsafe.implicits.global

final class Blueprint[A] private (val unsafeRun: () => A):

  // Transform the result without running the effect
  def map[B](f: A => B): Blueprint[B] =
    Blueprint(f(unsafeRun()))

  // Chain a computation that itself produces an IO
  def flatMap[B](f: A => Blueprint[B]): Blueprint[B] =
    Blueprint(f(unsafeRun()).unsafeRun())

object Blueprint:
  // Wrap a pure value — no side effects
  def pure[A](value: A): Blueprint[A] =
    Blueprint(value)

  // Wrap a side-effecting block, keeping it suspended
  def apply[A](block: => A): Blueprint[A] =
    new Blueprint(() => block)

def deleteProduction(target: String): Blueprint[Unit] =
  Blueprint(println(s"💥 BOOM! Production $target Deleted!"))

def orchrestrationForDoomDay(
    dangerousThing: => Blueprint[Unit],
    anotherDangerousThing: => Blueprint[Unit]
): Blueprint[String] =
  for
    _ <- Blueprint(println("Doing something dangerous..."))
    _ <- dangerousThing
    _ <- anotherDangerousThing
    _ <- Blueprint(println("Finished doing something dangerous."))
    state = "All clear... for now."
  yield state

val deleteProductionDatabase = deleteProduction("Database")
val deleteProductionInstance = deleteProduction("Instance")

val prepareToDestroy =
  orchrestrationForDoomDay(
    deleteProductionDatabase,
    deleteProductionInstance
  )

val destroyYourOwnProduct = prepareToDestroy

destroyYourOwnProduct
  .unsafeRun() // This is where the "bomb" actually goes off!