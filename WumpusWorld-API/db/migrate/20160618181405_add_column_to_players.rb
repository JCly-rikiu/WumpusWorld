class AddColumnToPlayers < ActiveRecord::Migration
  def change
    add_column :players, :arrow, :integer
  end
end
