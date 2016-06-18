class RemoveMapPlayers < ActiveRecord::Migration
  def change
    remove_column :players , :maps
  end
end
